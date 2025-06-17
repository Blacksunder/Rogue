package backend.datalayer;

import java.util.ArrayList;
import java.util.Comparator;

public class RecordKeeper {
    private ArrayList<SessionRecord> sessionRecords;

    public RecordKeeper() {
        sessionRecords = new ArrayList<>();
    }

    public void addRecord(SessionRecord sessionRecord) {
        sessionRecords.add(sessionRecord);
        if (sessionRecords.size() > 1) {
            sortRecords();
        }
    }

    public void sortRecords() {
        for (int i = 1; i < sessionRecords.size(); ++i) {
            for (int j = 1; j < sessionRecords.size(); ++j) {
                boolean coinsIsBigger = sessionRecords.get(j).getCoins() >
                        sessionRecords.get(j - 1).getCoins();
                boolean levelIsBigger = sessionRecords.get(j).getMaxLevel() >
                        sessionRecords.get(j - 1).getMaxLevel();
                boolean killsIsBigger = sessionRecords.get(j).getKills() >
                        sessionRecords.get(j - 1).getKills();
                boolean coinsIsEqual = sessionRecords.get(j).getCoins() ==
                        sessionRecords.get(j - 1).getCoins();
                boolean levelIsEqual = sessionRecords.get(j).getMaxLevel() ==
                        sessionRecords.get(j - 1).getMaxLevel();
                if (coinsIsBigger || (coinsIsEqual && levelIsBigger) || (coinsIsEqual &&
                        levelIsEqual && killsIsBigger)) {
                    SessionRecord tmp = sessionRecords.get(j - 1);
                    sessionRecords.set(j - 1, sessionRecords.get(j));
                    sessionRecords.set(j, tmp);
                }
            }
        }
        if (sessionRecords.size() > 10) {
            sessionRecords.remove(sessionRecords.size() - 1);
        }
    }

    public ArrayList<String> getTopRecords() {
        ArrayList<String> topRecordsOutput = new ArrayList<>();
        for (SessionRecord sessionRecord : sessionRecords) {
            topRecordsOutput.add(sessionRecord.toString());
        }
        return topRecordsOutput;
    }
}
