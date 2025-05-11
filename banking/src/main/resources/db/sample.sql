INSERT INTO "categories" ("id", "name")
VALUES (1, 'personal'),
       (2, 'retail'),
       (3, 'manufacturing'),
       (4, 'healthcare'),
       (5, 'financial services'),
       (6, 'real estate'),
       (7, 'technology'),
       (8, 'hospitality'),
       (9, 'education'),
       (10, 'logistics'),
       (11, 'construction'),
       (12, 'agriculture'),
       (13, 'automotive'),
       (14, 'consulting'),
       (15, 'wholesale'),
       (16, 'energy');


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



INSERT INTO "accounts" ("id", "name", "balance", "is_active", "account_number", "owner_id", "owner_type")
VALUES
--     Client accounts with 0
(1, 'admin savings', 20000.000, true, '11111111', 1, 0),
(2, 'testuser savings', 20000.000, true, '22222222', 2, 0),
(3, 'campaign owner savings', 20000.000, true, '33333333', 3, 0),
(4, 'Moudhi savings', 20000.000, true, '44444444', 4, 0),
(5, 'Omar owner savings', 20000.000, true, '55555555', 5, 0),

-- Campaign accounts
(6, 'Moudhis campaign', 4000.000, true, '99999999', 5, 1),

-- Campaign will default
(7, 'campaign will default', 2.000, true, '00001111', 2, 1);

INSERT INTO "transactions" ("id", "source_account", "destination_account", "amount", "created_at", "category_id",
                            "type")
VALUES
    -- From admins savings account to Moudhis campaign account
    (1, 1, 6, 1000.000, '2025-05-11 00:00:00', 2, 1),
    -- From testuser savings account to Moudhis campaign account
    (2, 2, 6, 1000.000, '2025-05-11 00:00:00', 2, 1),
    -- From Omars savings account to Moudhis campaign account
    (3, 5, 6, 1000.000, '2025-05-11 00:00:00', 2, 1),

    -- From admins savings account to account of campaign will default
    (4, 1, 7, 1000.000, '2025-05-11 00:00:00', 2, 1),
    -- From testuser savings to account of campaign will default
    (5, 2, 7, 1000.000, '2025-05-11 00:00:00', 2, 1),
    -- From Omars savings account to account of campaign will default
    (7, 5, 7, 1000.000, '2025-05-11 00:00:00', 2, 1);


