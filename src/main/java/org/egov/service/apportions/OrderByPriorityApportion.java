package org.egov.service.apportions;

import org.egov.service.Apportion;
import org.egov.tracer.model.CustomException;
import org.egov.web.enums.Purpose;
import org.egov.web.models.BillAccountDetail;
import org.egov.web.models.BillDetail;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.egov.util.ApportionConstants.*;

@Service
public class OrderByPriorityApportion implements Apportion {

    @Override
    public String getBusinessService() {
        return DEFAULT;
    }


    /**
     * 1. Sort the billDetails based on fromPeriod
     * 2. For each billDetail sort the BillAccountDetails by order
     * 3. Start apportioning by assigning positive adjustedAmount for negative amounts
     *    and negative adjustmentAmount for postive amounts
     * 4. If any advance amount is remaining new BillAccountDetail is created for it
     *    and assigned to last BillDetail
     * @param billDetails The list of BillDetail to be apportioned
     * @param amountPaid The total amount paid against the list of billDetails
     * @return
     */
    @Override
    public List<BillDetail> apportionPaidAmount(List<BillDetail> billDetails,BigDecimal amountPaid) {
        billDetails.sort(Comparator.comparing(BillDetail::getFromPeriod));
        BigDecimal remainingAmount = amountPaid;
        BigDecimal amount;
        Boolean isAmountPositive;

        for (BillDetail billDetail : billDetails){
            billDetail.getBillAccountDetails().sort(Comparator.comparing(BillAccountDetail::getOrder));
            validateOrder(billDetails);
            for(BillAccountDetail billAccountDetail : billDetail.getBillAccountDetails()) {
                amount = billAccountDetail.getAmount();
                isAmountPositive = amount.compareTo(BigDecimal.ZERO) >= 0;

                if (isAmountPositive) {

                    if (remainingAmount.equals(BigDecimal.ZERO)) {
                        billAccountDetail.setAdjustedAmount(BigDecimal.ZERO);
                        continue;
                    }

                    if (remainingAmount.compareTo(amount) <= 0) {
                        billAccountDetail.setAdjustedAmount(remainingAmount.negate());
                        remainingAmount = BigDecimal.ZERO;
                    }

                    if (remainingAmount.compareTo(amount) > 0) {
                        billAccountDetail.setAdjustedAmount(amount.negate());
                        remainingAmount = remainingAmount.subtract(amount);
                    }
                }
                else {
                    billAccountDetail.setAdjustedAmount(amount.negate());
                    remainingAmount = remainingAmount.subtract(amount);
                }
            }

            if(remainingAmount.compareTo(BigDecimal.ZERO)>0){
                BillAccountDetail billAccountDetailForAdvance = new BillAccountDetail();
                billAccountDetailForAdvance.setAmount(remainingAmount);
                billAccountDetailForAdvance.setPurpose(Purpose.ADVANCE_AMOUNT);
                billDetails.get(billDetails.size()-1).getBillAccountDetails().add(billAccountDetailForAdvance);
            }
        }
        return billDetails;
    }


    private void validateOrder(List<BillDetail> billDetails){
        Map<String,String> errorMap = new HashMap<>();
        billDetails.forEach(billDetail -> {
            billDetail.getBillAccountDetails().forEach(billAccountDetail -> {
                if(billAccountDetail.getOrder()==null)
                    errorMap.put("INVALID ORDER","Order is null for: "+billAccountDetail.getId());
            });
        });
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }


}
