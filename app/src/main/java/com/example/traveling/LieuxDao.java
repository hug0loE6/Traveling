package com.example.traveling;

import androidx.room.*;

import java.util.List;

@Dao
public interface LieuxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Lieux l);

    @Query("DELETE FROM lieux")
    void deleteAll();

    @Query("SELECT * FROM lieux")
    List<Lieux> getAll();
}
