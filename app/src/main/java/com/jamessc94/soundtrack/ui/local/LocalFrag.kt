package com.jamessc94.soundtrack.ui.local

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.jamessc94.soundtrack.PREF_SONG_SORT_ORDER
import com.jamessc94.soundtrack.R
import com.jamessc94.soundtrack.SongSortOrder
import com.jamessc94.soundtrack.databinding.FragLocalBinding
import com.jamessc94.soundtrack.media.callbacks.SortMenuListener
import com.jamessc94.soundtrack.media.ext.filter
import com.jamessc94.soundtrack.media.ext.getExtraBundle
import com.jamessc94.soundtrack.media.ext.toSongIds
import com.jamessc94.soundtrack.model.Songs
import com.jamessc94.soundtrack.model.Songs.Companion.SONGS_ALBUM
import com.jamessc94.soundtrack.model.Songs.Companion.SONGS_ALBUM_ID
import com.jamessc94.soundtrack.model.Songs.Companion.SONGS_ARTIST
import com.jamessc94.soundtrack.model.Songs.Companion.SONGS_ARTIST_ID
import com.jamessc94.soundtrack.model.Songs.Companion.SONGS_DURATION
import com.jamessc94.soundtrack.model.Songs.Companion.SONGS_MEDIA_ID
import com.jamessc94.soundtrack.model.Songs.Companion.SONGS_TITLE
import com.jamessc94.soundtrack.model.Songs.Companion.SONGS_TRACKNUMBER
import com.jamessc94.soundtrack.permission.Permission
import com.jamessc94.soundtrack.permission.PermissionManager
import com.jamessc94.soundtrack.permission.granted
import com.jamessc94.soundtrack.save
import com.jamessc94.soundtrack.ui.main.MainVM
import com.jamessc94.soundtrack.ui.main.NowPlayingVM
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LocalFrag : Fragment() {

    private lateinit var binding: FragLocalBinding
    val vm : LocalVM by viewModels()
    val vm_main : MainVM by viewModels()
    val vm_now : NowPlayingVM by viewModels()
    lateinit var adapterLocal : LocalAdap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragLocalBinding.inflate(inflater, container, false)

        PermissionManager.from(this)
            .request(Permission.Storage)
            .rationale(getString(R.string.permission_required_to_load_local_song))
            .checkPermission {
                granted.postValue(it)

                if(it){
                    adapterLocal = LocalAdap(this, vm_now, SongsAdapListener {
                        adapterLocal.getSongForPosition(it)?.let { song ->
                            val extras = getExtraBundle(adapterLocal.songs.toSongIds(), getString(R.string.all_songs))
                            vm_main.mediaItemClicked(song, extras)

                        }

                    }).apply {
                        showHeader = true
                        popupMenuListener = vm_main.popupMenuListener
                        sortMenuListener = sortListener

                    }

                    binding.fragLocalRv.apply {
                        adapter = adapterLocal

                    }

                    vm.mediaItems
                        .filter { it.isNotEmpty() }
                        .observe(this) { list ->
                            @Suppress("UNCHECKED_CAST")

                            adapterLocal.updateData(list.map {
                                val l = it.description.extras!!

                                Songs(
                                    id = l.getLong(SONGS_MEDIA_ID),
                                    albumId = l.getLong(SONGS_ALBUM_ID),
                                    artistId = l.getLong(SONGS_ARTIST_ID),
                                    title = l.getString(SONGS_TITLE, ""),
                                    artist = l.getString(SONGS_ARTIST, ""),
                                    album = l.getString(SONGS_ALBUM, ""),
                                    duration = l.getInt(SONGS_DURATION),
                                    trackNumber = l.getInt(SONGS_TRACKNUMBER)
                                )
                            })

                        }

                }else{
                    Timber.i("Errors failed")
                }
            }

        return binding.root

    }

    private val sortListener = object : SortMenuListener {
        override fun shuffleAll() {
            adapterLocal.songs.shuffled().apply {
                val extras = getExtraBundle(toSongIds(), getString(R.string.all_songs))

                if (this.isEmpty()) {
                    Snackbar.make(binding.fragLocalRv, R.string.shuffle_no_songs_error, Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    vm_main.mediaItemClicked(this[0], extras)
                }
            }
        }

        override fun sortAZ() = vm.sortOrderPref.save(PREF_SONG_SORT_ORDER, SongSortOrder.SONG_A_Z)

        override fun sortDuration() = vm.sortOrderPref.save(PREF_SONG_SORT_ORDER,SongSortOrder.SONG_DURATION)

        override fun sortYear() = vm.sortOrderPref.save(PREF_SONG_SORT_ORDER,SongSortOrder.SONG_YEAR)

        override fun numOfSongs() {}

        override fun sortZA() = vm.sortOrderPref.save(PREF_SONG_SORT_ORDER, SongSortOrder.SONG_Z_A)
    }

}