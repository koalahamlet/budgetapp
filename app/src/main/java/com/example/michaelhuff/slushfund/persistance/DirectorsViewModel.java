package com.example.michaelhuff.slushfund.persistance;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

public abstract class DirectorsViewModel extends AndroidViewModel {
    public DirectorsViewModel(@NonNull Application application) {
        super(application);
    }
//    private DirectorDao directorDao;
//    private LiveData<List<Director>> directorsLiveData;
//    public DirectorsViewModel(@NonNull Application application) {
//        super(application);
//        directorDao = MoviesDatabase.getDatabase(application).directorDao();
//        directorsLiveData = directorDao.getAllDirectors();
//    }
//    public LiveData<List<Director>> getDirectorList() {
//        return directorsLiveData;
//    }
//    public void insert(Director... directors) {
//        directorDao.insert(directors);
//    }
//    public void update(Director director) {
//        directorDao.update(director);
//    }
//    public void deleteAll() {
//        directorDao.deleteAll();
//    }
}