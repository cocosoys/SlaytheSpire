package soys.mods.slaythespire.client.hud;

public final class EndTurnButtonState {
    private static int x;
    private static int y;
    private static int width;
    private static int height;
    private static boolean visible;

    private EndTurnButtonState() {
    }

    public static void update(int x, int y, int width, int height, boolean visible) {
        EndTurnButtonState.x = x;
        EndTurnButtonState.y = y;
        EndTurnButtonState.width = width;
        EndTurnButtonState.height = height;
        EndTurnButtonState.visible = visible;
    }

    public static void hide() {
        visible = false;
    }

    public static boolean contains(double mouseX, double mouseY) {
        return visible
                && mouseX >= x
                && mouseX <= x + width
                && mouseY >= y
                && mouseY <= y + height;
    }
}
