package org.egov.service;

import org.egov.producer.Producer;
import org.egov.util.ApportionUtil;
import org.egov.web.models.ApportionRequest;
import org.egov.web.models.BillDetail;
import org.egov.web.models.BillInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import static org.egov.util.ApportionConstants.*;


@Service
public class ApportionService {

    private final List<Apportion> apportions;
    private Map<String,Apportion> APPORTION_MAP = new HashMap<>();

    private ApportionUtil util;
    private Producer producer;


    @Autowired
    public ApportionService(List<Apportion> apportions,ApportionUtil util,Producer producer) {
        this.apportions = Collections.unmodifiableList(apportions);
        this.util = util;
        this.producer = producer;
        initialize();
    }

    private void initialize(){
        if(Objects.isNull(apportions))
            throw new IllegalStateException("No Apportion found, spring initialization failed.");

        if(APPORTION_MAP.isEmpty() && !apportions.isEmpty()){
            apportions.forEach(apportion -> {
                APPORTION_MAP.put(apportion.getBusinessService(),apportion);
            });
        }
        APPORTION_MAP = Collections.unmodifiableMap(APPORTION_MAP);
    }


    /**
     * Apportions the paid amount for the given list of bills
     * @param request The apportion request
     * @return Apportioned Bills
     */
    public List<BillInfo> apportionBills(ApportionRequest request){
        List<BillInfo> billInfos = request.getBills();
        Apportion apportion;

        for(BillInfo billInfo : billInfos){
            Map<String,List<BillDetail>> businessServiceToBillDetails = util.groupByBusinessService(billInfo.getBillDetails());
            for(Map.Entry<String,BigDecimal> entry : billInfo.getCollectionMap().entrySet()){
                List<BillDetail> billDetails = businessServiceToBillDetails.get(entry.getKey());

                if(CollectionUtils.isEmpty(billDetails))
                    continue;

                if(isApportionPresent(entry.getKey()))
                    apportion = getApportion(entry.getKey());
                else apportion = getApportion(DEFAULT);

                apportion.apportionPaidAmount(billDetails,entry.getValue());
            }
        }
        return billInfos;
    }


    /**
     * Retrives the apportion for the given businessService
     * @param businessService The businessService of the billDetails
     * @return Apportion object for the given businessService
     */
    private Apportion getApportion(String businessService){
        return APPORTION_MAP.get(businessService);
    }


    /**
     * Checks if the apportion is present for the given businessService
     * @param businessService The businessService of the billDetails
     * @return True if the apportion is present else false
     */
    private Boolean isApportionPresent(String businessService){
        return APPORTION_MAP.containsKey(businessService);
    }



}
