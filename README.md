# <img width="100" height="100" alt="image" src="https://github.com/user-attachments/assets/0d696a5b-d886-4930-a506-6022b5555b1f" /> Super Hexagon (Java Edition)

This project is a small but intense **Java arcade game**, inspired by *Super Hexagon*.  
It was built as part of my **Advanced Programming** course at **Sharif University of Technology**,  
where I wanted to mix clean object-oriented design with a bit of creative fun.

---

##  What This Project Is About

The goal was simple but ambitious:  
build a **complete mini-game from scratch**, with no fancy game engines — just **pure Java and Swing**.

It became a playground for:
- Practicing **OOP design** and class management in a real-world-style project  
- Learning how to design a **graphical user interface**  
- Handling **animations, user input, and real-time logic** smoothly  

---

##  Game Idea

You control a tiny **triangle cursor** orbiting a rotating polygon in the center of the screen.  
Walls keep closing in from every direction — your only job is to **stay alive** by rotating left or right.  
The longer you survive, the faster and crazier it gets.

>  No levels, no breaks — just you, the rhythm, and your reflexes.

---

##  Core Features

###  Main Menu
- **Start Game:** jump right into action  
- **Best Record:** see your top survival time  
- **History:** review all your past runs  
- **Settings:** toggle music or auto-save  
- **Exit:** gracefully quit the game  

###  Gameplay
- Rotate with arrow keys (`←` / `→`) and mouse.(switch by pressing M)
- Dynamic **color shifts**, **rotating background**, and **speed increases** over time  
- Procedural obstacle patterns — never the same twice  
- Collision detection ends the run immediately  

###  Game History
All past games are saved (using **GSON** or **Jackson**) with player name, date, and duration.  
You can scroll through them in a simple history panel.

###  Settings
Switch the **theme music** on/off, and decide if games should be saved automatically.

## Gameplay Demo

<video src="superhexagon_v2.mp4" width="600" controls></video>


