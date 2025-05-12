INSERT INTO "categories" ( "name")
VALUES ( 'personal'),
       ('retail'),
       ( 'manufacturing'),
       ( 'healthcare'),
       ( 'financial services'),
       ( 'real estate'),
       ( 'technology'),
       ( 'hospitality'),
       ( 'education'),
       ( 'logistics'),
       ( 'construction'),
       ( 'agriculture'),
       ( 'automotive'),
       ( 'consulting'),
       ( 'wholesale'),
       ( 'energy');


INSERT INTO "kycs" ("user_id",
                    "first_name",
                    "last_name",
                    "date_of_birth",
                    "nationality",
                    "salary")
VALUES (1, 'Django', 'SuperUser', '1994-01-01', 'Kuwaiti', 2000),
       (2, 'Test', 'User', '1994-01-01', 'Kuwaiti', 2000),
       (3, 'Campaign', 'Owner', '1994-01-01', 'Kuwaiti', 2000),
       (4, 'Omar', 'AlIbraheem', '1994-01-01', 'Kuwaiti', 2000),
       (5, 'Moudhi', 'Albanani', '2000-01-01', 'Kuwaiti', 1200);



INSERT INTO "accounts" ("name", "balance", "is_active", "account_number", "owner_id", "owner_type")
VALUES
--     Client accounts with 0
( 'admin savings', 20000.000, true, '11111111', 1, 0),
( 'testuser savings', 20000.000, true, '22222222', 2, 0),
( 'campaign owner savings', 20000.000, true, '33333333', 3, 0),
( 'Moudhi savings', 20000.000, true, '44444444', 4, 0),
( 'Omar owner savings', 20000.000, true, '55555555', 5, 0),

-- Campaign accounts
( 'Moudhis campaign', 4000.000, true, '99999999', 4, 1),

-- Campaign will default
( 'campaign will default', 2.000, true, '00001111', 2, 1);

INSERT INTO "transactions" ( "source_account", "destination_account", "amount", "created_at", "category_id",
                            "type")
VALUES
    -- From admins savings account to Moudhis campaign account
    ( 1, 6, 1000.000, '2025-05-11 00:00:00', 2, 1),
    -- From testuser savings account to Moudhis campaign account
    ( 2, 6, 1000.000, '2025-05-11 00:00:00', 2, 1),
    -- From Omars savings account to Moudhis campaign account
    ( 5, 6, 1000.000, '2025-05-11 00:00:00', 2, 1),

    -- From admins savings account to account of campaign will default
    ( 1, 7, 1000.000, '2025-05-11 00:00:00', 2, 1),
    -- From testuser savings to account of campaign will default
    ( 2, 7, 1000.000, '2025-05-11 00:00:00', 2, 1),
    -- From Omars savings account to account of campaign will default
    ( 5, 7, 1000.000, '2025-05-11 00:00:00', 2, 1);


