Create Table Account(
ACCOUNT_ID INT auto_increment primary key,
USER_NAME varchar(100) unique key not null,
PASSWORD varchar(1000) not null,
ACCOUNT_TYPE varchar(100) not null,
EMAIL_ID varchar(200) not null,
PARENT_ACCOUNT_ID INT,
CREATION_DATE datetime,
SHARE_HISTORICAL_ORDERS boolean,
INITIAL_DATE_TO_SHARE datetime
);

Create Table ORDERS(
ORDER_ID INT auto_increment primary key,
TOTAL_COST double not null,
ACCOUNT_ID INT not null,
ORDER_STATUS varchar(100) not null,
ORDER_DATE datetime not null,
FOREIGN KEY (ACCOUNT_ID) REFERENCES Account(ACCOUNT_ID)
);

Create Table INVITATION(
INVITATION_ID INT auto_increment primary key,
BUSINESS_OWNER_ACCOUNT_ID INT not null,
INDIVIDUAL_ACCOUNT_ID INT not null,
INVITATION_STATUS varchar(100) not null,
SHARE_HISTORICAL_DATA boolean,
CREATION_DATE datetime,
ACTION_PERFORMED_DATE datetime
);

commit;
