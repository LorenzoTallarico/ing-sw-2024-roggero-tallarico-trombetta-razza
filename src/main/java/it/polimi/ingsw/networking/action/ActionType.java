package it.polimi.ingsw.networking.action;

public enum ActionType {
    CONNECTION,
    SETNICKNAME,
    ASKINGSTART,
    JOININGPLAYER,
    START,
    STARTERROR,

    ASKINGPLAYERSNUMBER,
    CHOSENPLAYERSNUMBER,

    CHATMESSAGE,
    WHOLECHAT,
    ASKINGCHAT,

    HAND,
    CHOOSEABLEACHIEVEMENTS,
    CHOSENACHIEVEMENT,

    ASKINGPLACE,
    PLACEDCARD,
    PLACINGCARD,
    PLACEDCARDERROR,

    CHOSENDRAWCARD,
    CARDDRAWN,
    ASKINGDRAW,

    CHOOSESIDESTARTERCARD,
    CHOSENSIDESTARTERCARD,

    DISCONNECTEDPLAYER,
    RECONNECTIONSUCCESS,
    RECONNECTEDPLAYER,
    PING,
    PONG,
    WAIT,


    WINNERS
}
