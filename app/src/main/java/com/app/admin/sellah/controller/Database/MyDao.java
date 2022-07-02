package com.app.admin.sellah.controller.Database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyDao
{

    @Insert
    public void addSearch(UserSearch userSearch);

    @Query("Select * from USER_SEARCH")
    List<UserSearch> getSearchedList();

    @Delete
    public void deleteSearch(UserSearch userSearch);

}
