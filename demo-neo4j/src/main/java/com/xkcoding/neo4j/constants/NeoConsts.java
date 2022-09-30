package com.xkcoding.neo4j.constants;

/**
 * <p>
 * Constant pool
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 14:45
 */
public interface NeoConsts {
    /**
     * Relationship: Class owned students
     */
    String R_STUDENT_OF_CLASS = "R_STUDENT_OF_CLASS";

    /**
     * Relationship: The class teacher of the class
     */
    String R_BOSS_OF_CLASS = "R_BOSS_OF_CLASS";

    /**
     * Relationship: The teacher of the course
     */
    String R_TEACHER_OF_LESSON = "R_TEACHER_OF_LESSON";

    /**
     * Relationship: The class selected by the student
     */
    String R_LESSON_OF_STUDENT = "R_LESSON_OF_STUDENT";

}
