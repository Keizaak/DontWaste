package fr.iutbm.dontwaste;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MealDAO {

    @Query("SELECT * FROM meals")
    List<Meal> getAllMeals();

    @Insert
    void insertMeals(Meal... meals);

}

