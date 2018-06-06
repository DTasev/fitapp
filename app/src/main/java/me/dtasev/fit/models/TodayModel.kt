package me.dtasev.fit.models

import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

data class Exercise(val name: String) : Serializable
data class ExerciseSet(val id: Int, val kgs: Float, val reps: Int) : Serializable
data class WorkoutExercise(val id: Int, val exercise: Exercise, val sets: MutableList<ExerciseSet>) : Serializable
data class Workout(val id: Int, val date: String,
                   val primaryMuscleGroup: String, val secondaryMuscleGroup: String,
                   val complete: Boolean, val startTime: String?, val endTime: String?) : Serializable {
    lateinit var primaryExercises: List<WorkoutExercise>
    lateinit var secondaryExercises: List<WorkoutExercise>
}

class TodayModel(private val response: JSONObject) {

    val workout: Workout = Workout(response["id"].toString().toInt(), response["date"].toString(),
            response["primary_muscle_group"].toString(), response["secondary_muscle_group"].toString(),
            response["completed"].toString().toBoolean(), response["start_time"].toString(), response["end_time"].toString())

    init {
        workout.primaryExercises = extractExercises("primary_exercises")
        workout.secondaryExercises = extractExercises("secondary_exercises")
    }

    private fun extractExercises(jsonKey: String): List<WorkoutExercise> {
        val primaryExercises = response[jsonKey] as JSONArray
        return List(primaryExercises.length()) {
            val exerciseSets = primaryExercises.getJSONObject(it)

            val exercise = Exercise((exerciseSets["exercise"] as JSONObject)["name"].toString())

            val setsArray = exerciseSets["sets"] as JSONArray
            val setsList = MutableList(setsArray.length()) {
                val setObject = setsArray[it] as JSONObject
                ExerciseSet(setObject["id"].toString().toInt(),
                        setObject["kgs"].toString().toFloat(),
                        setObject["reps"].toString().toInt())
            }
            WorkoutExercise(exerciseSets["id"].toString().toInt(), exercise, setsList)
        }
    }

    //    fun date(): String {
//        return response["date"].toString()
//    }
//
//    fun primaryMuscleGroup(): String {
//        return response["primary_muscle_group"].toString()
//    }
//
//    fun secondaryMuscleGroup(): String {
//        return response["secondary_muscle_group"].toString()
//    }
//
//    fun workoutExerciseSet(): List<WorkoutExercise> {
//        return workoutExercises
//    }
//
    fun primaryExercisesNamesList(): List<String> {
        return List(workout.primaryExercises.size) {
            workout.primaryExercises[it].exercise.name
        }
    }

    fun secondaryExercisesNamesList(): List<String> {
        return List(workout.secondaryExercises.size) {
            workout.secondaryExercises[it].exercise.name
        }
    }
}