CREATE TABLE eg_appr_bills_request(
  billInfo JSONB,
  tenantId character varying(64),
  billId character varying(64),
  mobileNumber character varying(64),
  createdBy character varying(64),
  createdTime bigint
 );

 CREATE TABLE eg_appr_bills_response(
   billInfo JSONB,
   tenantId character varying(64),
   billId character varying(64),
   mobileNumber character varying(64),
   createdBy character varying(64),
   createdTime bigint
 );