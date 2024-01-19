<div align="center">

# 🚘🚦🚘 Traffic light simulator 🚘🚦🚘

  <p align="center">
    Software for calculation and visualisation of optimized traffic lights in randomly generated cities
    <br />
    <i>Project done for an engineering thesis</i>
    <br/>
    <b>Date of completion: 📆 28.01.2023 📆</b>
  </p>

</div>

## About The Project

**Thesis title:** Modelowanie i symulacja ruchu ulicznego: analiza i optymalizacja (_Traffic modeling and simulation: analysis and optimization_)

This program deals with the application of an evolutionary algorithm to optimize the traffic throughput by adjusting traffic lights accordingly. An application has been developed that allows the traffic light system to be configured in such a way, so that the average travel time of vehicles is as short as possible. For this task, an algorithm was used genetic algorithm, which, after previously generating or loading a stored grid of city streets, can reduce the waiting time of cars in traffic congestion by several times. The program detects areas with increased traffic intensity and independently finds effective solutions using today's popular traffic light change patterns such as the so-called "green waves" or e.g., less frequent changing of traffic lights on the outskirts of cities. Thanks to these measures, the travel time of cars passing in those areas has decreased.

[Thesis information](https://sin.put.poznan.pl/theses/details/t54722)

## Features

- Create your own simulation configuration
- Visualization of simulation results
- Changing the speed of visualization
- Changing camera position and zooming in and out


## Demo

![image](https://github.com/marcol13/traffic-light-simulator/assets/56632321/a43ea400-dea0-4fb9-bce9-7e35b239958c)

[2024-01-19-21-49-27.webm](https://github.com/marcol13/traffic-light-simulator/assets/56632321/a32db623-7dda-43ab-b332-2d65db098c77)


### Before optimization
[przed_optymalizacja.webm](https://github.com/marcol13/traffic-light-simulator/assets/56632321/982c2bd3-8f69-4dec-b4a1-633e82644393)


### After optimization
[po_optymalizacji.webm](https://github.com/marcol13/traffic-light-simulator/assets/56632321/55d452a4-b759-46d6-b73b-99d97214d7dd)



## Technologies Used

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![LibGDX](https://img.shields.io/badge/LibGDX-%23EB0443.svg?style=for-the-badge&logoColor=white)


## Getting started

Keep in mind that the project was created only to study the optimization of lights and visualize the created simulations. It is not a full-fledged application and no further contributions or adaptation of the product for broader use is envisioned here.

1. Clone repository: `git clone https://github.com/marcol13/traffic-light-simulator`
2. Set your own configuration in `/core/src/com/put/urbantraffic/Settings.java`
3. Run `/desktop/src/com/put/urbantraffic/DesktopLauncher.java`


## Contributors
[@marcol13](https://github.com/marcol13)

[@kezc](https://github.com/kezc)

[@mackurzawa](https://github.com/mackurzawa)

[@PawelZ4krzewski](https://github.com/PawelZ4krzewski)
