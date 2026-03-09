-- Create a stored procedure named getUserInformation
CREATE PROCEDURE getUserInformation @email NVARCHAR(255) AS BEGIN -- Set NOCOUNT ON to prevent extra result sets from interfering with SELECT statements.
SET NOCOUNT ON;
    -- Query the Users and Profiles tables using a JOIN
    SELECT TOP 2
        p.profile_id,
        u.username,
        u.email,
        p.fitness_level,
        p.goal
    FROM
        Users u
    INNER JOIN
        Profiles p ON u.user_id = p.user_id
    WHERE
        u.email = @email
    ORDER BY
        u.created_at DESC;
END;
GO

-- Execution Example
EXEC getUserInformation @email  = 'user@example.com';
GO



----------------------------------------------------------------------------------------------

-- Create stored procedure to retrieve nutrition plan
CREATE PROCEDURE GetUserNutritionPlan
    @email VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP 1
        p.profile_id,
        u.email,
        p.goal,
        dp.protein_grams AS protein,
        dp.carbs_grams AS carbohydrates,
        dp.fats_grams AS fats,
        dp.target_calories AS total_calories,
        p.fitness_level
    FROM
        users u
    INNER JOIN
        profiles p ON u.user_id = p.user_id
    INNER JOIN
        diet_plans dp ON p.profile_id = dp.profile_id
    WHERE
        u.email = @email
        AND dp.is_active = 1
    ORDER BY
        dp.start_date DESC;
END;
GO

-- Execution Example
EXEC GetUserNutritionPlan @email = 'user@example.com';
GO


----------------------------------------------------------------------------------------------


-- Create stored procedure to retrieve weekly weight averages
CREATE PROCEDURE GetUserWeeklyAverages
    @email VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        u.username,
        DATEPART(ISO_WEEK, dwl.log_date) AS week_number,
        -- Calculate approx week start (Monday)
        DATEADD(wk, DATEDIFF(wk, 0, dwl.log_date), 0) AS week_start_date,
        -- Calculate approx week end (Sunday)
        DATEADD(wk, DATEDIFF(wk, 0, dwl.log_date), 6) AS week_end_date,
        CAST(AVG(dwl.weight_kg) AS DECIMAL(5,2)) AS weekly_average
    FROM
        users u
    INNER JOIN
        profiles p ON u.user_id = p.user_id
    INNER JOIN
        daily_weight_logs dwl ON p.profile_id = dwl.profile_id
    WHERE
        u.email = @email
    GROUP BY
        u.username,
        DATEPART(YEAR, dwl.log_date),
        DATEPART(ISO_WEEK, dwl.log_date),
        DATEADD(wk, DATEDIFF(wk, 0, dwl.log_date), 0),
        DATEADD(wk, DATEDIFF(wk, 0, dwl.log_date), 6)
    ORDER BY
        week_start_date ASC;
END;
GO

-- Execution Example
EXEC GetUserWeeklyAverages @email = 'user@example.com';
GO



----------------------------------------------------------------------------------------------

-- Create stored procedure to retrieve the current active workout plan
CREATE PROCEDURE GetUserCurrentWorkoutPlan
    @email VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        p.profile_id,
        u.email,
        pc.training_split AS split_name,
        pc.training_days,
        -- Aggregate weak points (comma-separated)
        (SELECT STRING_AGG(muscle_group, ', ') 
         FROM context_weak_points cwp 
         WHERE cwp.context_id = pc.context_id) AS weak_points,
        -- Aggregate restrictions as machine_preferences (comma-separated)
        (SELECT STRING_AGG(restriction_text, ', ') 
         FROM context_restrictions cr 
         WHERE cr.context_id = pc.context_id) AS machine_preferences,
        wd.day_index AS day_label,
        wd.split_type AS day_focus,
        e.name AS exercise_name,
        we.sets,
        CAST(we.min_reps AS VARCHAR(10)) + '-' + CAST(we.max_reps AS VARCHAR(10)) AS reps,
        '60-90s' AS rest_time
    FROM
        users u
    INNER JOIN
        profiles p ON u.user_id = p.user_id
    INNER JOIN
        workout_plans wp ON p.profile_id = wp.profile_id
    INNER JOIN
        workout_days wd ON wp.plan_id = wd.plan_id
    INNER JOIN
        workout_exercises we ON wd.day_id = we.day_id
    INNER JOIN
        exercises e ON we.exercise_id = e.exercise_id
    -- Get the latest plan context for meta-info (assuming 1:1 or latest preference applies)
    OUTER APPLY (
        SELECT TOP 1 * 
        FROM plan_contexts pc2 
        WHERE pc2.profile_id = p.profile_id 
        ORDER BY pc2.created_at DESC
    ) pc
    WHERE
        u.email = @email
        AND wp.is_active = 1
    ORDER BY
        wd.day_index ASC,
        we.order_index ASC;
END;
GO

-- Execution Example
EXEC GetUserCurrentWorkoutPlan @email = 'user@example.com';
GO





