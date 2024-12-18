#!/bin/bash

# Set variables
GITLAB_TOKEN=""
GIT_REPO="git@git.epam.com:oleksandr_orlov/java-lab.git"
BRANCH="cucumber_testing_integrational"
CLONE_DIR="java-lab-clone"
TARGET_DIR="src/main/resources"

# Create a directory to clone the repo
mkdir -p $CLONE_DIR
cd $CLONE_DIR

# Clone the repository
echo "Cloning repository from $GIT_REPO..."
git clone --single-branch --branch $BRANCH $GIT_REPO .

# Navigate to Gym_Trainer_Workload and Spring_Core_GYM
echo "Cloning Gym_Trainer_Workload and Spring_Core_GYM..."
git sparse-checkout init --cone
git sparse-checkout set Gym_Trainer_Workload Spring_Core_GYM

# Run mvn clean package in both microservices
echo "Building Gym_Trainer_Workload..."
cd Gym_Trainer_Workload
mvn clean package -DskipTests=true
cd ../

echo "Building Spring_Core_GYM..."
cd Spring_Core_GYM
mvn clean package -DskipTests=true
cd ../

# Copy the JAR files to the target directory
echo "Copying JAR files to $TARGET_DIR..."
cd ../

cp $CLONE_DIR/Gym_Trainer_Workload/target/*.jar $TARGET_DIR
cp $CLONE_DIR/Spring_Core_GYM/target/*.jar $TARGET_DIR

rm -rf $CLONE_DIR

echo "Process completed."
