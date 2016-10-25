/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */
///siron
//s
package com.naman14.timber.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.adapters.ArtistAdapter;
import com.naman14.timber.dataloaders.ArtistLoader;
import com.naman14.timber.models.ActivityHelper;
import com.naman14.timber.models.Artist;
import com.naman14.timber.models.FileUtil;
import com.naman14.timber.models.Prompt;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.utils.SortOrder;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.DividerItemDecoration;
import com.naman14.timber.widgets.FastScroller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

///siron
public class FolderFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArtistAdapter mAdapter;
    private ListView recyclerView;
    private GridLayoutManager layoutManager;
    private RecyclerView.ItemDecoration itemDecoration;
    private PreferencesUtility mPreferences;
    private boolean isGrid;
    private File mCurrentPath = null;

    //siron
    private List<CharSequence> mNames;
    private List<String> mPaths;
    private MediaPlayer mp = new MediaPlayer();
    private int currentPosition = 0;
//
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtility.getInstance(getActivity());
        isGrid = mPreferences.isArtistsInGrid();
        String currentPath = null;

        if (savedInstanceState != null) {
            currentPath = savedInstanceState.getString(ActivityHelper.Keys.SEARCH_PATH);
        }

        if (currentPath != null) {
            mCurrentPath = new File(currentPath);
        } else {
            // Pick the root of the storage directory by default
            mCurrentPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.scan_roms_activity, container, false);

        recyclerView = (ListView) rootView.findViewById(R.id.listView);

        //setLayoutManager();

        if (getActivity() != null) {
            PopulateFileList();
            //new loadArtists().execute("");
        }
        return rootView;
    }

    /*
    private void setLayoutManager() {
        if (isGrid) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
            fastScroller.setVisibility(View.GONE);
        } else {
            layoutManager = new GridLayoutManager(getActivity(), 1);
            fastScroller.setVisibility(View.VISIBLE);
            fastScroller.setRecyclerView(recyclerView);
        }
        recyclerView.setLayoutManager(layoutManager);
    }
*/
    /*
    private void setItemDecoration() {
        if (isGrid) {
            int spacingInPixels = getActivity().getResources().getDimensionPixelSize(R.dimen.spacing_card_album_grid);
            itemDecoration = new SpacesItemDecoration(spacingInPixels);
        } else {
            itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        }
        recyclerView.addItemDecoration(itemDecoration);
    }

    private void updateLayoutManager(int column) {
        recyclerView.removeItemDecoration(itemDecoration);
        recyclerView.setAdapter(new ArtistAdapter(getActivity(), ArtistLoader.getAllArtists(getActivity())));
        layoutManager.setSpanCount(column);
        layoutManager.requestLayout();
        setItemDecoration();
    }
*/
    private void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                List<Artist> artistList = ArtistLoader.getAllArtists(getActivity());
                mAdapter.updateDataSet(artistList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.artist_sort_by, menu);
        inflater.inflate(R.menu.menu_show_as, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_A_Z);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_za:
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_Z_A);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_number_of_songs:
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_SONGS);
                reloadAdapter();
                return true;
            case R.id.menu_sort_by_number_of_albums:
                mPreferences.setArtistSortOrder(SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_ALBUMS);
                reloadAdapter();
                return true;
            case R.id.menu_show_as_list:
                mPreferences.setArtistsInGrid(false);
                isGrid = false;
                //updateLayoutManager(1);
                return true;
            case R.id.menu_show_as_grid:
                mPreferences.setArtistsInGrid(true);
                isGrid = true;
                //updateLayoutManager(2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void PopulateFileList() {
        //setTitle( mCurrentPath.getPath() );
        // Populate the file list
        // Get the filenames and absolute paths
        mNames = new ArrayList<CharSequence>();
        mPaths = new ArrayList<String>();
        FileUtil.populate(mCurrentPath, true, true, true, mNames, mPaths);

        if (mCurrentPath.isDirectory()) {
            //ListView listView1 = (ListView) findViewById( R.id.listView1 );
            ArrayAdapter<String> adapter = Prompt.createFilenameAdapter(getContext(), mPaths, mNames);
            recyclerView.setAdapter(adapter);
            //listView1.setAdapter( adapter );
            recyclerView.setOnItemClickListener(this);
            recyclerView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    Log.d("LongPress", "yes");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Options")
                            .setItems(R.array.options, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    switch (which) {
                                        case 0 :
                                            MusicPlayer.clearQueue();
                                            if(MusicPlayer.isPlaying()) {
                                                MusicPlayer.playOrPause();
                                            }
                                            MusicPlayer.openFile(mPaths.get(position));
                                            MusicPlayer.playOrPause();
                                            break;
                                        case 1 :
                                            Uri uri = MediaStore.Audio.Media.getContentUriForPath(mPaths.get(position));
                                            RingtoneManager.setActualDefaultRingtoneUri(
                                                    getActivity(),
                                                    RingtoneManager.TYPE_RINGTONE,
                                                    uri
                                            );
                                            Toast.makeText(getContext(), "Ringtone has been set", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 2 :
                                            Intent intentShareFile = new Intent(Intent.ACTION_SEND);

                                            intentShareFile.setType("application/pdf");
                                            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + mPaths.get(position)));

                                            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                                    "Sharing File...");
                                            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                                            startActivity(Intent.createChooser(intentShareFile, "Share File"));
                                            break;
                                    }
                                }
                            });
                    if(mPaths.get(position).contains(".mp3")) {
                        builder.create().show();
                    } else {
                        Toast.makeText(getContext(), "Incompatible file format", Toast.LENGTH_SHORT).show();
                    }
                    //Inflating the Popup using xml fil
                    return true;
                }
            });
        }
    }

    private class loadArtists extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)
                mAdapter = new ArtistAdapter(getActivity(), ArtistLoader.getAllArtists(getActivity()));
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            //recyclerView.setAdapter(mAdapter);
            if (getActivity() != null) {
                //setItemDecoration();
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.top = space;
            outRect.right = space;
            outRect.bottom = space;

        }
    }
    //siron
    private void playSong(final String songPath) {
        //mCurrentPath = new File(mPaths.get(position));
        if (new File(songPath).isFile()) {
            if (songPath.contains(".mp3")) {
                MusicPlayer.clearQueue();
                if(MusicPlayer.isPlaying()) {
                    MusicPlayer.playOrPause();
                }
                MusicPlayer.openFile(songPath);
//                MusicPlayer.playNext(getContext(), );
                MusicPlayer.playOrPause();
//                MusicPlayer.addToQueue(getContext(), 1, -1, TimberUtils.IdType.NA);
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    public void onCompletion(MediaPlayer arg0) {
//                        nextSong();
//                    }
//                });
            }
        }
    }

    private void nextSong() {
        if (++currentPosition >= mPaths.size()) {
            currentPosition = 0;
        } else {
            playSong(mPaths.get(currentPosition));
        }
    }
///
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        mCurrentPath = new File(mPaths.get(position));
        //siron
        if (mCurrentPath.isDirectory()) {
            PopulateFileList();
        } else {
            playSong(mPaths.get(position));
            currentPosition = position;
        }
        //
    }
}
