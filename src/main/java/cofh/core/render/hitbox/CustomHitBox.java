package cofh.core.render.hitbox;

public class CustomHitBox {

    public boolean drawSide[] = { false, false, false, false, false, false };
    public double sideLength[] = { 0, 0, 0, 0, 0, 0 };
    public double middleHeight = 0;
    public double middleWidth = 0;
    public double middleDepth = 0;
    public double minX = 0;
    public double minY = 0;
    public double minZ = 0;

    public CustomHitBox(double middleHeight, double middleWidth, double middleDepth, double minX, double minY, double minZ) {

        this.middleDepth = middleDepth;
        this.middleHeight = middleHeight;
        this.middleWidth = middleWidth;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
    }

    public CustomHitBox drawSide(int side, boolean draw) {

        if (drawSide.length > side) {
            drawSide[side] = draw;
        }
        return this;
    }

    public CustomHitBox setSideLength(int side, double length) {

        if (sideLength.length > side) {
            sideLength[side] = length;
        }
        return this;
    }

    public CustomHitBox offsetForDraw(double x, double y, double z) {

        minX += x;
        minY += y;
        minZ += z;
        return this;
    }

    public CustomHitBox addExtraSpace(double extraSpace) {

        minX -= extraSpace;
        minY -= extraSpace;
        minZ -= extraSpace;
        middleDepth += extraSpace;
        middleHeight += extraSpace;
        middleWidth += extraSpace;

        for (int i = 0; i < sideLength.length; i++) {
            sideLength[i] += extraSpace;
        }
        return this;
    }

}
