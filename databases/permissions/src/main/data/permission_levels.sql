--
-- The initial set of permission levels.
--
INSERT INTO permission_levels (name, description, precedence) VALUES
    ('own', 'Implies that the user can assign permissions to, read, and modify a resource.', 0),
    ('write', 'Implies that the user can read and modify a resource.', 1),
    ('admin', 'Implies that a user can read and make limited motifications to a resource.', 2),
    ('read', 'Implies that a user can read a resource.', 3);
