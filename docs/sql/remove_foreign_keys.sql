-- 如果表已经存在外键约束，先删除外键约束
-- 查看现有外键约束：
-- SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE 
-- WHERE TABLE_NAME = 'y_user_note_interaction' AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 删除可能存在的外键约束（如果存在的话）
-- ALTER TABLE y_user_note_interaction DROP FOREIGN KEY fk_user_interaction_user_id;
-- ALTER TABLE y_user_note_interaction DROP FOREIGN KEY fk_user_interaction_note_id;

-- 或者使用动态SQL（需要存储过程）：
DELIMITER $$

CREATE PROCEDURE DropForeignKeysIfExists()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE constraint_name VARCHAR(64);
    DECLARE cur CURSOR FOR 
        SELECT CONSTRAINT_NAME 
        FROM information_schema.KEY_COLUMN_USAGE 
        WHERE TABLE_NAME = 'y_user_note_interaction' 
        AND TABLE_SCHEMA = DATABASE()
        AND REFERENCED_TABLE_NAME IS NOT NULL;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO constraint_name;
        IF done THEN
            LEAVE read_loop;
        END IF;
        SET @sql = CONCAT('ALTER TABLE y_user_note_interaction DROP FOREIGN KEY ', constraint_name);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END LOOP;
    CLOSE cur;
END$$

DELIMITER ;

-- 执行存储过程删除外键约束
CALL DropForeignKeysIfExists();

-- 删除存储过程
DROP PROCEDURE IF EXISTS DropForeignKeysIfExists;