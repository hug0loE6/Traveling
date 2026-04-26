package com.example.traveling;

import androidx.room.*;

import java.util.List;

@Dao
public interface LieuxDao {
    @Insert
    void insert(Lieux l);

    @Query("SELECT * FROM lieux")
    List<Lieux> getAllUsers();

    @Delete
    void delete(Lieux l);
}
