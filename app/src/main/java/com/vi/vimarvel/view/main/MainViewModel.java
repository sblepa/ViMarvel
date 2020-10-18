package com.vi.vimarvel.view.main;

import com.vi.vimarvel.dispatcher.ActionType;
import com.vi.vimarvel.dispatcher.Dispatcher;
import com.vi.vimarvel.dispatcher.EventErrorType;
import com.vi.vimarvel.dispatcher.EventType;
import com.vi.vimarvel.dispatcher.IEventHandler;
import com.vi.vimarvel.store.models.MarvelCharacterModel;

import java.util.ArrayList;

import androidx.annotation.VisibleForTesting;

public class MainViewModel implements IEventHandler<ArrayList<MarvelCharacterModel>> {

    private ArrayList<MarvelCharacterModel> marvelCharacters;
    private IMainViewModelViewEvents viewEvents;

    MainViewModel() {
        this(Dispatcher.getInstance());
    }

    @VisibleForTesting
    private MainViewModel(Dispatcher dispatcher) {
        // Register to listen to character fetched events
        dispatcher.addEventListener(EventType.EVENT_TYPES_MARVEL_CHARACTERS_FETCHED, this);

        // Send fetch marvel characters action
        dispatcher.dispatchAction(ActionType.ACTION_TYPE_FETCH_MARVEL_CHARACTERS);
    }

    /*
     * Event handling
     */

    @Override
    public void onEvent(EventType eventType, ArrayList<MarvelCharacterModel> data) {
        if (eventType == EventType.EVENT_TYPES_MARVEL_CHARACTERS_FETCHED) {
            marvelCharacters = data;
            if (viewEvents != null) {
                viewEvents.onDataUpdate();
            }
        }
    }

    @Override
    public void onEventFailure(EventType eventType, EventErrorType errorType, ArrayList<MarvelCharacterModel> data) {

    }

    /*
     * View Interface
     */

    interface IMainViewModelViewEvents {
        void onDataUpdate();
    }

    void setViewEvents(IMainViewModelViewEvents viewEvents) {
        this.viewEvents = viewEvents;
    }

    /*
     * Internal
     */

    public ArrayList<MarvelCharacterModel> getMarvelCharacters() {
        return marvelCharacters;
    }
}
