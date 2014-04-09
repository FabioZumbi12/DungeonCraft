

int getSchematicIndex(int x, int y, int z, int schematicWidth, int schematicLength, int schematicHeight)
{
    return (y * schematicWidth * schematicLength) + (z * schematicWidth) + x;
}

int getHeightKey(int x, int z, int schematicWidth, int schematicLength, int schematicHeight)
{
    return z * schematicWidth + x;
}

int getBlockOpaque(unsigned char block)
{
    switch(block)
    {
        case 1:
            return 15;
        case 0:
            return 0;
        default:
            return 0;
    }
}


void spreadSkyLight2(int face, int x, int y, int z, unsigned char lightLevel, int schematicWidth, int schematicLength, int schematicHeight,__global const unsigned char* blocks, __global const int* heightMap, __global unsigned char* skylight)
{
    if (lightLevel <= 0) {
        return;
    }
    int schematicIndex = getSchematicIndex(x, y, z, schematicWidth, schematicLength, schematicHeight);
    if (face != 0) {
        lightLevel -= getBlockOpaque(blocks[schematicIndex]);
        if (lightLevel <= 0) {
            lightLevel = 0;
        }
        if (lightLevel < skylight[schematicIndex]) {
            return;
        }
    }
    skylight[schematicIndex] = lightLevel;

    if (lightLevel <= 0) {
        return;
    }

    --lightLevel;
    if (face != 6 && x > 0) {
        spreadSkyLight2(5, x - 1, y, z, lightLevel, schematicWidth, schematicLength, schematicHeight,blocks, heightMap, skylight);
    }
    if (face != 5 && x < schematicWidth - 1) {
        spreadSkyLight2(6, x + 1, y, z, lightLevel, schematicWidth, schematicLength, schematicHeight,blocks, heightMap, skylight);
    }
    if (face != 4 && z > 0) {
        spreadSkyLight2(3, x, y, z - 1, lightLevel, schematicWidth, schematicLength, schematicHeight,blocks, heightMap, skylight);
    }
    if (face != 3 && z < schematicLength - 1) {
        spreadSkyLight2(4, x, y, z + 1, lightLevel, schematicWidth, schematicLength, schematicHeight,blocks, heightMap, skylight);
    }
    if (face != 1 && y > 0) {
        spreadSkyLight2(2, x, y - 1, z, lightLevel, schematicWidth, schematicLength, schematicHeight,blocks, heightMap, skylight);
    }
    if (face != 2 && y < schematicHeight - 1) {
        spreadSkyLight2(1, x, y + 1, z, lightLevel, schematicWidth, schematicLength, schematicHeight,blocks, heightMap, skylight);
    }
}

__kernel void calculateSkylight(__global const unsigned char* blocks, __global const int* heightMap, __global unsigned char* skylight, int schematicWidth, int schematicLength, int schematicHeight)
{
    int y, z;
    for (int x = 0; x < schematicWidth; x++) {
        for (z = 0; z < schematicLength; z++) {
            for (y = heightMap[getHeightKey(x, z, schematicWidth, schematicLength, schematicHeight)]; y < schematicHeight; y++) {
                spreadSkyLight2(0, x, y, z, (unsigned char) 15, schematicWidth, schematicLength, schematicHeight,blocks, heightMap, skylight);
            }
        }
    }
}

/* Directions:
    SELF -> 0
    UP -> 1
    DOWN -> 2
    NORTH -> 3
    SOUTH -> 4
    WEST -> 5
    EAST -> 6

*/
