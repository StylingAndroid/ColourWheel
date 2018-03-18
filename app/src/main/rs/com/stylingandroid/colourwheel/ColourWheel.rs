#pragma version(1)
#pragma rs java_package_name(com.stylingandroid.colourwheel)

#include "hsv.rsh"

const static uchar4 transparent = {0, 0, 0, 0};

float centreX;
float centreY;
float radius;
float brightness = 1.0f;

void colourWheel(rs_script script, rs_allocation allocation, float brightness_value) {
    centreX = rsAllocationGetDimX(allocation) / 2.0f;
    centreY = rsAllocationGetDimY(allocation) / 2.0f;
    radius = min(centreX, centreY);
    brightness = brightness_value;
    rsForEach(script, allocation, allocation);
}

uchar4 RS_KERNEL root(uchar4 in, int32_t x, int32_t y) {
    uchar4 out;
    float xOffset = x - centreX;
    float yOffset = y - centreY;
    float centreOffset = sqrt(xOffset * xOffset + yOffset * yOffset);
    if (centreOffset <= radius) {
        float centreAngle = fmod(degrees(atan2(yOffset, xOffset)) + 360.0f, 360.0f);
        float3 colourHsv;
        colourHsv.x = centreAngle;
        colourHsv.y = centreOffset / radius;
        colourHsv.z = brightness;
        out = rsPackColorTo8888(hsv2Argb(colourHsv, 255.0f));
    } else {
        out = transparent;
    }
    return out;
}
