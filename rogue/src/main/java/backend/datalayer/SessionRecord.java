package backend.datalayer;

public class SessionRecord {
    private int coins;
    private int maxLevel;
    private int kills;

    public SessionRecord(int coins, int maxLevel, int kills) {
        this.coins = coins;
        this.maxLevel = maxLevel;
        this.kills = kills;
    }

    public int getCoins() {
        return coins;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getKills() {
        return kills;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    @Override
    public String toString() {
        return "Collected coins: " + getCoins() + ", reached level: " + getMaxLevel() +
                ", killed monsters: " + getKills();
    }
}
