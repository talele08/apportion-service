package org.egov.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;

import org.egov.web.enums.Purpose;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * BillAccountDetail
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2019-02-25T15:07:36.183+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BillAccountDetail {
        @JsonProperty("id")
        private BigDecimal id = null;

        @JsonProperty("glcode")
        private String glcode = null;

        @JsonProperty("order")
        private Integer order = null;

        @JsonProperty("amount")
        private BigDecimal amount = null;

        @JsonProperty("adjustedAmount")
        private BigDecimal adjustedAmount;

        @JsonProperty("isActualDemand")
        private Boolean isActualDemand = null;

        @JsonProperty("tenantId")
        private String tenantId = null;

        @JsonProperty("billDetail")
        private String billDetail = null;

        @JsonProperty("demandDetailId")
        private String demandDetailId = null;

        @JsonProperty("purpose")
        private Purpose purpose;

        @JsonProperty("additionalDetails")
        private Object additionalDetails = null;



}

