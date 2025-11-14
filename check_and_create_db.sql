DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'gamerscove') THEN
        CREATE DATABASE gamerscove;
        RAISE NOTICE 'Database created successfully';
    ELSE
        RAISE NOTICE 'Database already exists';
    END IF;
END $$;
