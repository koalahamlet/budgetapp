package com.example.michaelhuff.slushfund.persistance;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = { Director.class}, version = 1)
public abstract class MovieDatabase extends RoomDatabase {
    private static MovieDatabase INSTANCE;
    private static final String DB_NAME = "movies.db";
    public static MovieDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MovieDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MovieDatabase.class, DB_NAME)
                            .allowMainThreadQueries() // SHOULD NOT BE USED IN PRODUCTION !!!
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
//                                    Log.d("MovieDatabase", "populating with data...");
//                                    new PopulateDbAsync(INSTANCE).execute();
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    public void clearDb() {
        if (INSTANCE != null) {
            //todo: do something
//            new PopulateDbAsync(INSTANCE).execute();
        }
    }
//    public abstract MovieDao movieDao();
    public abstract DirectorDao directorDao();
//    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
//        private final MovieDao movieDao;
//        private final DirectorDao directorDao;
//        public PopulateDbAsync(MovieDatabase instance) {
//            movieDao = instance.movieDao();
//            directorDao = instance.directorDao();
//        }
//        @Override
//        protected Void doInBackground(Void... voids) {
//            movieDao.deleteAll();
//            directorDao.deleteAll();
//            Director directorOne = new Director("Adam McKay");
//            Director directorTwo = new Director("Denis Villeneuve");
//            Director directorThree = new Director("Morten Tyldum");
//            Movie movieOne = new Movie("The Big Short", (int) directorDao.insert(directorOne));
//            final int dIdTwo = (int) directorDao.insert(directorTwo);
//            Movie movieTwo = new Movie("Arrival", dIdTwo);
//            Movie movieThree = new Movie("Blade Runner 2049", dIdTwo);
//            Movie movieFour = new Movie("Passengers", (int) directorDao.insert(directorThree));
//            movieDao.insert(movieOne, movieTwo, movieThree, movieFour);
//            return null;
//        }
//    }
}