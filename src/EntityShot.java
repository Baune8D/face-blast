public class EntityShot {
    private int direction;
    public int x;
    public int y;
    private int id;
    private Boolean enemyShot;

    public EntityShot(int direction, int x, int y, int id, Boolean shotState) {
        this.direction = direction;
        if (shotState.booleanValue()) {
            this.x = (x + 15);
            this.y = (y + 17);
        } else {
            this.x = (x + 8);
            this.y = (y + 13);
        }
        this.id = id;
        this.enemyShot = shotState;
    }

    public int getDirection() {
        return this.direction;
    }

    public int getID() {
        return this.id;
    }

    public Boolean checkEnemyShot() {
        return this.enemyShot;
    }
}