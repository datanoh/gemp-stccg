package com.gempukku.stccg.merchant;

import com.gempukku.stccg.cards.CardBlueprintLibrary;
import com.gempukku.stccg.db.MerchantDAO;
import com.gempukku.stccg.cards.SetDefinition;

import java.util.Date;

public class ParametrizedMerchant implements Merchant {
    private static final int BOOSTER_PRICE = 1000;
    private static final long DAY = 1000 * 60 * 60 * 24;

    private Date _merchantSetupDate;

    private final float _profitMargin = 0.7f;

    private MerchantDAO _merchantDao;
    private final CardBlueprintLibrary _library;

    public ParametrizedMerchant(CardBlueprintLibrary library) {
        _library = library;
    }

    public void setMerchantSetupDate(Date merchantSetupDate) {
        _merchantSetupDate = merchantSetupDate;
    }

    public void setMerchantDao(MerchantDAO merchantDao) {
        _merchantDao = merchantDao;
    }

    @Override
    public Integer getCardBuyPrice(String blueprintId, Date currentTime) {
        boolean foil = blueprintId.endsWith("*");

        blueprintId = _library.getBaseBlueprintId(blueprintId);

        Double normalPrice = getNormalPrice(blueprintId, currentTime);
        if (normalPrice == null)
            return null;

        int price = Math.max(1, (int) Math.floor(_profitMargin * normalPrice / getSetupComponent(currentTime)));

        if (foil)
            return 2 * price;
        return price;
    }

    @Override
    public Integer getCardSellPrice(String blueprintId, Date currentTime) {
        Double normalPrice = getNormalPrice(blueprintId, currentTime);
        if (normalPrice == null)
            return null;
        double setupComponent = getSetupComponent(currentTime);
        return Math.max(2, (int) Math.ceil(normalPrice * setupComponent));
    }

    private double getSetupComponent(Date currentTime) {
        long _easingTimeMs = 30 * DAY;
        if (currentTime.getTime() < _merchantSetupDate.getTime() + _easingTimeMs)
            return 1 + ((_easingTimeMs - (currentTime.getTime() - _merchantSetupDate.getTime())) / (5d * DAY));
        return 1;
    }

    private Double getNormalPrice(String blueprintId, Date currentTime) {
        MerchantDAO.Transaction lastTrans = _merchantDao.getLastTransaction(blueprintId);
        if (lastTrans == null) {
            Integer basePrice = getBasePrice(blueprintId);
            if (basePrice == null)
                return null;
            lastTrans = new MerchantDAO.Transaction(_merchantSetupDate, basePrice, MerchantDAO.TransactionType.SELL, 0);
        }
        long _priceRevertTimeMs = 5 * DAY;
        if (lastTrans.getDate().getTime() + _priceRevertTimeMs > currentTime.getTime()) {
            float _fluctuationValue = 0.1f;
            double _returnPriceSlope = 0.3;
            if (lastTrans.getTransactionType() == MerchantDAO.TransactionType.SELL) {
                return (1 + _fluctuationValue) * lastTrans.getPrice() / (1 + (_fluctuationValue * Math.pow(1f * (currentTime.getTime() - lastTrans.getDate().getTime()) / _priceRevertTimeMs, _returnPriceSlope)));
            } else {
                return (1 - _fluctuationValue) * lastTrans.getPrice() / (1 - (_fluctuationValue * Math.pow(1f * (currentTime.getTime() - lastTrans.getDate().getTime()) / _priceRevertTimeMs, _returnPriceSlope)));
            }
        }
        long timeSinceRevertMs = currentTime.getTime() - lastTrans.getDate().getTime() - _priceRevertTimeMs;
        //  (stored price)/(1+(fluctuation value * ms since last transaction)/(price revert time in ms))
        double _decreasePrizeSlope = 4;
        long _decreaseHalfedMs = 90 * DAY;
        return lastTrans.getPrice() / (1 + Math.pow(1f * timeSinceRevertMs / _decreaseHalfedMs, _decreasePrizeSlope));
    }

    private Integer getBasePrice(String blueprintId) {
        int underscoreIndex = blueprintId.indexOf("_");
        if (underscoreIndex == -1)
            return null;
        SetDefinition rarity = _library.getSetDefinitions().get(blueprintId.substring(0, blueprintId.indexOf("_")));
        String cardRarity = rarity.getCardRarity(blueprintId);
        if (cardRarity.equals("X"))
            return 3 * BOOSTER_PRICE;
        if (cardRarity.equals("R") || cardRarity.equals("P"))
            return BOOSTER_PRICE;
        if (cardRarity.equals("U") || cardRarity.equals("S"))
            return BOOSTER_PRICE / 3;
        if (cardRarity.equals("C"))
            return BOOSTER_PRICE / 7;
        throw new RuntimeException("Unknown rarity for priced card: " + cardRarity);
    }

    @Override
    public void cardBought(String blueprintId, Date currentTime, int price) {
        if (blueprintId.endsWith("*"))
            price = price / 2;
        blueprintId = _library.getBaseBlueprintId(blueprintId);
        _merchantDao.addTransaction(blueprintId, (price / _profitMargin), currentTime, MerchantDAO.TransactionType.BUY);
    }

    @Override
    public void cardSold(String blueprintId, Date currentTime, int price) {
        _merchantDao.addTransaction(blueprintId, price, currentTime, MerchantDAO.TransactionType.SELL);
    }
}
