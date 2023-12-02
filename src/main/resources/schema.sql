create table if not exists tb_purchase_transaction (
transaction_id uuid default random_uuid(),
description VARCHAR(50),
transaction_amount NUMERIC(100,2) not null,
transaction_date TIMESTAMP WITH TIME ZONE not null,
PRIMARY KEY (transaction_id)
);

create table if not exists tb_exchange_rate (
country VARCHAR,
currency VARCHAR,
rate NUMERIC(100,2) ,
effective_date TIMESTAMP,
PRIMARY KEY (country)
);
