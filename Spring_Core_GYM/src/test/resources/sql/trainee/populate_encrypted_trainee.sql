INSERT INTO training_type (training_type_name) VALUES ('testTrainingType1');
SET @training_type_id_1 = (SELECT ID FROM training_type ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, is_active)
VALUES ('Test', 'Trainer', 'testtrainer', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);

SET @trainer_id = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, is_active)
VALUES ('Test', 'Trainee', 'testtrainee', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @trainee_id = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, is_active)
VALUES ('Test', 'Trainee', 'testtrainee2', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @trainee2_id = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, is_active)
VALUES ('Test', 'Trainee', 'updateTrainee', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @updateTrainee_id = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, is_active)
VALUES ('Test', 'Trainee', 'deleteTrainee', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @deleteTrainee_id = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, is_active)
VALUES ('Test', 'Trainee', 'updateTraineeTrainersUser', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @updateTraineeTrainersUser_id = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, is_active)
VALUES ('Test', 'Trainee', 'activatedTrainee', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @activatedTrainee_id = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO gym_user (first_name, last_name, username, password, is_active)
VALUES ('Test', 'Trainee', 'deactivatedTrainee', '$2a$12$ZBHomvuHVeYrC50sdXu.JuO06hh8uuNrIkg73ABvSzEXQZFa3XrhG', true);
SET @deactivatedTrainee_id = (SELECT ID FROM gym_user ORDER BY ID DESC LIMIT 1);

INSERT INTO trainer (id, training_type_id)
VALUES (@trainer_id, @training_type_id_1);

INSERT INTO trainee (id, address, date_of_birth)
VALUES (@trainee_id, '123 Main St', '1990-01-01'),
       (@trainee2_id, '123 Main St', '1990-01-01'),
       (@updateTrainee_id, '123 Main St', '1990-01-01'),
       (@deleteTrainee_id, '123 Main St', '1990-01-01'),
       (@updateTraineeTrainersUser_id, '123 Main St', '1990-01-01'),
       (@activatedTrainee_id, '123 Main St', '1990-01-01'),
       (@deactivatedTrainee_id, '123 Main St', '1990-01-01');

INSERT INTO trainer_trainee (trainer_id, trainee_id)
VALUES (@trainer_id, @trainee_id);

INSERT INTO training (training_name, training_date, training_duration_minutes, trainee_id, trainer_id, training_type_id)
VALUES ('Training 1', '2024-10-01', 60, @trainee_id, @trainer_id, @training_type_id_1),
       ('Training 2', '2024-10-02', 60, @trainee_id, @trainer_id, @training_type_id_1);
