INSERT INTO "campaigns" ("created_by",
                         "category_id",
                         "title",
                         "description",
                         "goal_amount",
                         "interest_rate",
                         "repayment_months",
                         "status",
                         "submitted_at",
                         "approved_by",
                         "deadline",
                         "account_id",
                         "image_url")
VALUES (5, -- created_by (Moudhi)
        7, -- category_id (technology)
        'Moudhi’s Startup Fund',
        'Helping Moudhi complete her university degree in computer science.',
        5000.000, -- goal_amount
        0.0200, -- 2% interest rate
        12, -- repayment in 12 months
        4, -- status (e.g. 1 = funded)
        '2025-03-12', -- submitted_at
        1, -- approved_by (admin user_id = 1)
        '2025-06-12', -- deadline
        6, -- account_id (Moudhi’s campaign account)
        'http://localhost:9000/loanlift-public/3a5fa2bf-8737-41d3-bd36-4f17ab4efd13' -- image_url
       ),
       (2, -- created_by (testuser)
        7, -- category_id (technology)
        'SAS Service Campaign',
        'Raising funds to expand product stock in a local retail shop.',
        3000.000, -- goal_amount
        0.0200, -- 2% interest rate
        6, -- repayment in 6 months
        4, -- status (funded)
        '2025-03-12', -- submitted_at
        1, -- approved_by (admin)
        '2025-06-01', -- deadline
        7, -- account_id
        'http://localhost:9000/loanlift-public/3a5fa2bf-8737-41d3-bd36-4f17ab4efd13' -- image_url
       );


INSERT INTO "comments" ("created_by", "campaign_id", "created_at", "message")
VALUES
-- Comment 1: By user 2 (testuser) on campaign 1 (Moudhi's campaign)
(2, 1, '2025-05-12', 'This is a great initiative! Best of luck.'),

-- Comment 2: By user 4 (Moudhi) on campaign 2 (SAS Service Campaign)
(4, 2, '2025-05-12', 'Appreciate the support from everyone involved.'),

-- Comment 3: By user 5 (Omar) on campaign 1 — will receive replies
(5, 1, '2025-05-12', 'How will the funds be used specifically?');


INSERT INTO "replies" ("comment_id", "created_at", "message")
VALUES
-- Replies to comment 3 (Omar's question)
(3, '2025-05-12', 'Great question! Most of the funds will go toward tuition.'),
(3, '2025-05-12', 'We also plan to invest in software tools and certifications.');


-- Comment 4: by admin on campaign 2
INSERT INTO "comments" ("created_by", "campaign_id", "created_at", "message")
VALUES (1, 2, '2025-05-12', 'What sets this campaign apart from others in the same category?');

-- Comment 5: by owner on campaign 1
INSERT INTO "comments" ("created_by", "campaign_id", "created_at", "message")
VALUES (3, 1, '2025-05-12', 'Will there be regular updates on progress?');

-- Comment 6: by testuser on campaign 1
INSERT INTO "comments" ("created_by", "campaign_id", "created_at", "message")
VALUES (2, 1, '2025-05-12', 'Happy to support a fellow tech enthusiast!');

-- Reply to comment 3 (Omar's question)
INSERT INTO "replies" ("comment_id", "created_at", "message")
VALUES (3, '2025-05-12', 'Most of the funds will go toward tuition and tools.');

-- Reply to comment 4 (admin's question)
INSERT INTO "replies" ("comment_id", "created_at", "message")
VALUES (4, '2025-05-12', 'We’re targeting underserved rural tech support with institutional backing.');

-- Reply to comment 5 (owner’s question)
INSERT INTO "replies" ("comment_id", "created_at", "message")
VALUES (5, '2025-05-12', 'Yes, monthly updates will be shared on the platform.');
-- Pledges to Moudhi’s Startup Fund (campaign_id = 1)
INSERT INTO "pledges" ("id", "user_id", "account_id", "campaign_id", "amount", "created_at", "updated_at", "status", "withdrawn_at", "commited_at")
VALUES
    (1, 1, 1, 1, 1000.000, '2025-05-11', '2025-05-11', 0, NULL, '2025-05-11'), -- Admin
    (2, 2, 2, 1, 1000.000, '2025-05-11', '2025-05-11', 0, NULL, '2025-05-11'), -- Testuser
    (3, 5, 5, 1, 1000.000, '2025-05-11', '2025-05-11', 0, NULL, '2025-05-11'); -- Omar

-- For Moudhi’s Startup Fund (campaign_id = 1)
INSERT INTO "pledge_transactions" ("id", "transaction_id", "pledge_id", "type")
VALUES
    (1, 1, 1, 0),  -- Admin -> Moudhi’s campaign
    (2, 2, 2, 0),  -- Testuser -> Moudhi’s campaign
    (3, 3, 3, 0);  -- Omar -> Moudhi’s campaign


-- Pledges to SAS Service Campaign (campaign_id = 2)
INSERT INTO "pledges" ("id", "user_id", "account_id", "campaign_id", "amount", "created_at", "updated_at", "status", "withdrawn_at", "commited_at")
VALUES
    (4, 1, 1, 2, 1000.000, '2025-05-11', '2025-05-11', 0, NULL, '2025-05-11'), -- Admin
    (5, 2, 2, 2, 1000.000, '2025-05-11', '2025-05-11', 0, NULL, '2025-05-11'), -- Testuser
    (6, 5, 5, 2, 1000.000, '2025-05-11', '2025-05-11', 0, NULL, '2025-05-11'); -- Omar

-- For SAS Service Campaign (campaign_id = 2)
INSERT INTO "pledge_transactions" ("id", "transaction_id", "pledge_id", "type")
VALUES
    (4, 4, 4, 0),  -- Admin -> SAS campaign
    (5, 5, 5, 0),  -- Testuser -> SAS campaign
    (6, 7, 6, 0);  -- Omar -> SAS campaign