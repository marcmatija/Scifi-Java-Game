package net.casqan.scifigame.dungeon;

import name.panitz.game2d.GameObj;
import name.panitz.game2d.Vertex;
import net.casqan.scifigame.Game2D;
import net.casqan.scifigame.core.Event;
import net.casqan.scifigame.core.Layers;
import net.casqan.scifigame.entities.*;
import net.casqan.scifigame.extensions.Rect;
import net.casqan.scifigame.extensions.VertexInt;
import net.casqan.scifigame.tilesystem.*;
import net.casqan.scifigame.ui.UILabel;
import net.casqan.scifigame.ui.UIRectangle;
import net.casqan.scifigame.ui.UIStyle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.casqan.scifigame.Game2D.*;

public class Room {
    public VertexInt position;
    public Tilemap tilemap;
    public Tileset tileset;
    public Environment environment;
    public RoomType type;
    public int width;
    public int height;
    public int seed;
    public List<Corridor> openDirections;
    public List<Wall> walls;
    public List<Door> doors;
    public Room(){}
    public Room(VertexInt position){
        this.position = position;
    }
    public Room(VertexInt position, int width, int height,int seed,Tileset tileset,List<Corridor> openDirections){
        this.position = position;
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.tileset = tileset;
        this.openDirections = openDirections;
    }
    public void BuildRoom(){
        tilemap = new Tilemap(tileset,null,4);
        walls = new ArrayList<>();
        doors = new ArrayList<>();
        String[][] map = new String[width][height];
        var keys = tileset.tiles.keySet().toArray();
        boolean[][] collision = new boolean[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                    collision[x][y] = true;
                }
            }
        }
        int w = width / 2 - 1;
        int h = height / 2 - 1;

        var up = openDirections.stream().filter(
                (c -> c.direction().equals(new VertexInt(0,-1))))
                .findFirst();
        if (up.isPresent()){
            for (int x = w; x < w + 2; x++) {
                collision[x][0] = false;
            }
            if(up.get().isExit()){
                var pos = new Vertex(
                        position.x * width * tileset.tileWidth * tilemap.scale,
                        position.y * height * tileset.tileHeight * tilemap.scale);
                pos.x += w * tileset.tileHeight * tilemap.scale;
                doors.add(new Door( pos,
                        tileset.tileWidth * tilemap.scale * 2,
                        tileset.tileHeight * tilemap.scale,
                        tileset.GetTile("0;0").image,
                        up.get().child()));
            }
        }
        var down = openDirections.stream().filter(
                c -> c.direction().equals(new VertexInt(0,1)))
                .findFirst();
        if (down.isPresent()){
            for (int x = w; x < w + 2; x++) {
                collision[x][height - 1] = false;
            }
            if (down.get().isExit()){
                var pos = new Vertex(
                        position.x * width * tileset.tileWidth * tilemap.scale,
                        position.y * height * tileset.tileHeight * tilemap.scale);
                pos.x += w * tileset.tileHeight * tilemap.scale;
                pos.y += (height - 1) * tileset.tileHeight * tilemap.scale;
                doors.add(new Door( pos,
                        tileset.tileWidth * tilemap.scale * 2,
                        tileset.tileHeight * tilemap.scale,
                        tileset.GetTile("0;0").image,
                        down.get().child()));
            }
        }
        var right = openDirections.stream().filter((
                c -> c.direction().equals(new VertexInt(1,0))))
                .findFirst();
        if (right.isPresent()){
            for (int y = h; y < h + 2; y++) {
                collision[width - 1][y] = false;
            }
            if(right.get().isExit()) {
                var pos = new Vertex(
                        position.x * width * tileset.tileWidth * tilemap.scale,
                        position.y * height * tileset.tileHeight * tilemap.scale);
                pos.x += (width - 1) * tileset.tileHeight * tilemap.scale;
                pos.y += h * tileset.tileHeight * tilemap.scale;
                doors.add(new Door(pos,
                        tileset.tileWidth * tilemap.scale,
                        tileset.tileHeight * tilemap.scale * 2,
                        tileset.GetTile("0;0").image,
                        right.get().child()));
            }
        }
        var left = openDirections.stream().filter((
                c -> c.direction().equals(new VertexInt(-1,0))))
                .findFirst();
        if (left.isPresent()){
            for (int y = h; y < h + 2; y++) {
                collision[0][y] = false;
            }
            if (left.get().isExit()){
                var pos = new Vertex(
                        position.x * width * tileset.tileWidth * tilemap.scale,
                        position.y * height * tileset.tileHeight * tilemap.scale);
                pos.y += h * tileset.tileHeight * tilemap.scale;
                doors.add(new Door( pos,
                        tileset.tileWidth * tilemap.scale,
                        tileset.tileHeight * tilemap.scale * 2,
                        tileset.GetTile("0;0").image,
                        left.get().child()));
            }
        }

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                map[x][y] = (String) keys[Game2D.Random().nextInt(keys.length)];
            }
        }

        tilemap.map = map;
        tilemap.BuildTileMap();
        environment = new Environment(tilemap);
        environment.pos.x = position.x * width * tileset.tileWidth * tilemap.scale;
        environment.pos.y = position.y * height * tileset.tileHeight * tilemap.scale;

        //Build walls
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                if (collision[x][y]){
                    var pos = new Vertex(environment.pos.x + x * tileset.tileWidth * tilemap.scale,
                            environment.pos.y + y * tileset.tileHeight * tilemap.scale);
                    Wall wall = new Wall(pos,tileset.tileWidth * tilemap.scale,
                            tileset.tileHeight * tilemap.scale, null);
                    walls.add(wall);
                }
            }
        }

        for(GameObj wall : walls){
            Instantiate(Layers.L_STATICS,wall);
        }
        for (var door : doors){
            Instantiate(Layers.L_STATICS,door);
        }
        Instantiate(Layers.L_ENVIRONMENT,environment);
        Populate();
    }

    public void Populate(){
        System.out.println(type);
        if (type == null){
            if (doors.size() < 1 ){
                type = RoomType.End;
            }else if (Random().nextFloat() > .8f){
                type = RoomType.Merchant;
            }else {
                type = RoomType.Normal;
            }
        }
        System.out.println(type);
        var worldPos = new Vertex(
                position.x * width * tileset.tileWidth * tilemap.scale,
                position.y * height * tileset.tileHeight * tilemap.scale);
        switch (type){
            case Boss:
                var _pos = Vertex.add(new Vertex(16*4*7,16*4*5),worldPos);
                var boss = (Boss) PREFABS.get("END_BOSS");
                Instantiate(Layers.L_ENTITIES, boss,_pos);
                boss.setpos(_pos);
                var bossUIStyle = new UIStyle();
                bossUIStyle.backgroundColor = Color.magenta;
                bossUIStyle.color = Color.white;
                var bossHealthContainerStyle = new UIStyle();
                bossHealthContainerStyle.backgroundColor = Color.black;
                var healthContainer = new UIRectangle(new Rect(0,6,208,28),new Vertex(0.5,0),bossHealthContainerStyle);
                var healthBar = new UIRectangle(new Rect(0,10,200,20),new Vertex(0.5,0),bossUIStyle);
                var healthBarText = new UILabel("Corrupted Crystal",new Rect(0,50,0,0),new Vertex(0.5,0),bossUIStyle);
                Instantiate(Layers.L_UI,healthContainer);
                Instantiate(Layers.L_UI,healthBar);
                Instantiate(Layers.L_UI,healthBarText);
                boss.onDamage.AddListener((b) -> healthBar.rect.dimensions.x =
                        boss.statistics.get(Statistics.HEALTH) / 500f * 200);
                break;
            case End:
                break;
            case Merchant:
                var _merchant = ((Merchant) PREFABS.get("MERCHANT")).Clone();
                _merchant.SetItems(getInstance().items.values());
                Instantiate(Layers.L_ENTITIES, _merchant,Vertex.add(new Vertex(16*4*8,16*4*8),worldPos));
            case Start:
                SpawnKeysInRoom();
                break;
            case Normal:
                var count = Random().nextInt(5,10);
                List<Enemy> enemies = new ArrayList<>();
                for (int i = 0;i < count;i++){
                    var sf = tileset.tileWidth * tilemap.scale;//scaling factor
                    var pos = new Vertex(
                            position.x * width * sf,
                        position.y * height * sf);
                    var obj = PREFABS.get("enemy");
                    pos.x += Random().nextFloat() * (height - 3) * sf - obj.anchor().x;
                    pos.y += Random().nextFloat() * (width - 3) * sf - obj.anchor().y;
                    pos.x += sf;
                    pos.y += sf;
                    var en = new Enemy((Enemy) obj,pos);
                    enemies.add(en);
                    Instantiate(Layers.L_ENTITIES,en);
                }
                for (int i = 0; i < doors.size(); i++){
                    enemies.get(i).onDeath = new Event<>();
                    enemies.get(i).onDeath.AddListener((var) -> Instantiate(Layers.L_ENTITIES,
                            new Key(getInstance().keyEntity,
                                    Vertex.add(var.pos,var.anchor))));
                }
                break;
            default:
                System.out.println("Unknown room type. This should never happen. How did this happen?");
        }
    }

    private void SpawnKeysInRoom() {
        for (int i = 0; i < doors.size(); i++){
            var pos = new Vertex(
                    position.x * width * tileset.tileWidth * tilemap.scale,
                    position.y * height * tileset.tileHeight * tilemap.scale);
            pos.x += Random().nextFloat() * (width - 2) * tileset.tileWidth * tilemap.scale;
            pos.y += Random().nextFloat() * (width - 2) * tileset.tileWidth * tilemap.scale;
            pos.x += tileset.tileWidth * tilemap.scale;
            pos.y += tileset.tileWidth * tilemap.scale;
            Instantiate(Layers.L_ENTITIES,new Key(getInstance().keyEntity,pos));
        }
    }
}
