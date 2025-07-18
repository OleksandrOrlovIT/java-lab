INSERT INTO training_type (training_type_name) VALUES ('testTrainingType1');
SET @training_type_id_1 = (SELECT ID FROM  training_type ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, active)
VALUES ('Test1', 'Trainer1', 'testtrainer1', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @trainer_id_1 = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, active)
VALUES ('Test2', 'Trainer2', 'testtrainer2', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @trainer_id_2 = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, active)
VALUES ('Test1', 'Trainer1', 'updateTrainer', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @updateTrainer_id = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, active)
VALUES ('Test1', 'Trainer1', 'activatedTrainer', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @activatedTrainer_id = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, active)
VALUES ('Test1', 'Trainer1', 'deactivatedTrainer', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', false);
SET @deactivatedTrainer_id = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, active)
VALUES ('Test1', 'Trainee2', 'testtrainee1', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @trainee_id_1 = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, active)
VALUES ('Test2', 'Trainee2', 'testtrainee2', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @trainee_id_2 = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO trainer (id, training_type_id)
VALUES (@trainer_id_1, @training_type_id_1),
       (@trainer_id_2, @training_type_id_1),
       (@updateTrainer_id, @training_type_id_1),
       (@activatedTrainer_id, @training_type_id_1),
       (@deactivatedTrainer_id, @training_type_id_1);

INSERT INTO trainee (id, address, date_of_birth)
VALUES (@trainee_id_1, '123 Main St', '1990-01-01'),
       (@trainee_id_2, '123 Main St', '1990-01-01');

INSERT INTO trainer_trainee (trainer_id, trainee_id)
VALUES (@trainer_id_1, @trainee_id_1),
       (@trainer_id_2, @trainee_id_1);

INSERT INTO training (training_name, training_date, training_duration_minutes, trainee_id, trainer_id, training_type_id)
VALUES ('Training 1', '2024-10-01', 60, @trainee_id_1, @trainer_id_1, @training_type_id_1),
       ('Training 2', '2024-10-02', 60, @trainee_id_1, @trainer_id_1, @training_type_id_1);
