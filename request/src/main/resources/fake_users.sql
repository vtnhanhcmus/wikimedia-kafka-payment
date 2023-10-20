-- Create a function to generate fake users
CREATE OR REPLACE FUNCTION generate_fake_users()
RETURNS VOID AS $$
DECLARE
i INT;
BEGIN
FOR i IN 1..1000 LOOP
        -- Generate random values for email and username
        INSERT INTO users (email, username)
        VALUES (concat('user', i, '@example.com'), 'user' || i);
END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Execute the function to generate fake users
SELECT generate_fake_users();
