ALTER TABLE user_roles
    ADD CONSTRAINT fk_user
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE CASCADE,
    ADD CONSTRAINT fk_role
        FOREIGN KEY (role_id) REFERENCES roles(id)
            ON DELETE CASCADE;
