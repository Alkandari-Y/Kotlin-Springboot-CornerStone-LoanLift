
INSERT INTO public.roles (name)
VALUES
    ('ROLE_USER'),
    ('ROLE_ADMIN'),
    ('ROLE_DEVELOPER');


-- Insert users with admin first
INSERT INTO "users" ("id", "civil_id", "username", "email", "password", "created_at", "updated_at", "is_active")
VALUES
(1, '111111111111', 'admin', 'admin@gmail.com', '$2a$10$KzdrH.oslwtuaKvF4Q/X1OZFawsndKNhJI0kC8rdjD/LdbSEXG9yK', '2025-05-11', '2025-05-11', false),
(2, '222222222222', 'testuser', 'testuser@snddev.com', '$2a$10$LZseq/6Q2MMKQHsqwLdVzO6EBBx.1K2CEcMUmA3LZOlpeG0p2kktu', '2025-05-11', '2025-05-11', false),
(3, '333333333333', 'owner', 'owner@gmail.com', '$2a$10$bhqL4lj.ofkMRshUqglrKeETgCuMjKyez7EGhpes9k.uTxrdx/rjW', '2025-05-11', '2025-05-11', false);

-- Insert all users with role 1 and give admin role 2 as well
INSERT INTO "user_roles" ("user_id", "role_id")
VALUES
(1, 1),  -- admin with role 1
(1, 2),  -- admin with role 2
(2, 1),  -- testuser with role 1
(3, 1);  -- owner with role 1