DELIMITER $$

CREATE PROCEDURE GenerateUsers()
BEGIN
    DECLARE i INT DEFAULT 0;

    WHILE i < 1000 DO
        -- Generate random email and username
        SET @rand_email = CONCAT('user', FLOOR(RAND() * 10000), '@example.com');
        SET @rand_username = CONCAT('username', FLOOR(RAND() * 10000));

        -- Insert the random data into the 'users' table
        INSERT INTO users (email, username) VALUES (@rand_email, @rand_username);

        SET i = i + 1;
    END WHILE;

END $$

DELIMITER ;

-- Call the procedure to generate users
CALL GenerateUsers();