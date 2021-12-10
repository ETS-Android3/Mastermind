# Mastermind

## Table of Contents
1. [Overview](#Overview)
2. [Development](#Development)
3. [Running Mastermind](#Run)
4. [Playing Mastermind](#Play)

## Overview <a name="Overview"></a>
### Description
An Android mobile application based on the code breaking board game Mastermind. 

*You wake up. Trapped in a cursed mansion. The only way to escape is to figure out the secret code. 
<br>Do you have what it takes to **crack the code** and escape the cursed mansion?*

### Walkthrough 
<img src="walkthrough.gif" width=250>

## Development <a name="Development"></a>
### Understand Expectations
Before beginning the development process, I wanted to understand all expected details of the product. I analyzed the implementation guide carefully and noted deadlines, requirements, and optional implementations. I consolidated a list of concepts I was unfamiliar with, such as Integer Generator API, and another list of concepts I am familiar with that may be needed for product development. I also determined any unclear expectations that need further clarification.

* Need to Research:
  * Mastermind gameplay and UI design
  * dynamic view creation (ex. buttons)
  * pop up window
* Fammiliar Concepts:
  * AsyncHTTPClient for API
  * design and develop interactive UI
  * develop game (ex. Connect Four)
* Unclear Expectations: 
  * If secret code is "1234" and guess is "1156", would user guess have a location match for the first "1" and a value match for the second "1"? Or would it only take either the location match or value match? 
  * If secret code is "1234" and guess is "2222", would match priority be based on location or which match comes first?

### Research
During my research on Mastermind itself, I evaluated how the traditional gameplay matches the expected implementation. I also deducted a temporary answer to the unclear expectations found in the implementation guide. Although I had a temporary answer, I noted that it still needs to be clarified with the project manager or team lead.

Additionally, I explored concepts I was unfamiliar with, such as creating a pop up window, and how to make the game more accessible for people with disabilities. I investigated which colors were differentiable for people with colorblindness as well as button placements for those who may have physically impairments.

* Researched:
  * Mastermind gameplay 
  * Mastermind UI design as board games, PC games, mobile games
  * dynamic view creation (ex. buttons)
  * pop up window
  * accessibility (ex. colorblindness, physical impairment)
 
### Brainstorm


### Wireframe
<img src="wireframe.jpg" width=400>

### Planning
* Plan

### Building & Debugging
* Build

### Creative Extensions
#### Implemented
* Create haunting and tense atmosphere through background, color scheme, and music
* Build option to choose difficulty level: easy, normal, challenge
* Build timer for challenge level to increase difficulty and tension
* Add haunting background music for easy and normal levels
* Add tense background music that intensifies as time runs out for challenge level

#### Attempted
* Data persistence on orientation change (will continue working on in future)

#### Future Implementations
* Menu screen with option to start game or view rules
* Rules screen explaining rules of the game and user interface guide
* Continue testing user interface with various devices (ex. tablet, iOS devices)

## Running Mastermind <a name="Run"></a>
### How to Run Code
* Download Android Studio: https://developer.android.com/studio
* Download this Mastermind repository from GitHub
* Open repository on Android Studio with minSDK 21 or above
* Create virtual mobile device (ex. Pixel 3a API 30)
* Run and install application on virtual device 

### How to Run on Mobile Device
* Download Mastermind.apk on mobile device: 
  <br>https://drive.google.com/file/d/1oxS7ogRBJMdFFZulqrvkCfko0vlO4KSY/view?usp=sharing
* Locate Mastermind.apk (in downloads or where downloads are stored)
* Install Mastermind.apk 

## Playing Mastermind <a name="Play"></a>
### How to Play
* Objective: *crack the secret code and escape the cursed mansion*
* Make sure device has sound on
* Open Mastermind application
* Guess a 4 digit code with the possible numbers provided
* Submit guess to check matches
* Analyze past guesses and matches (see UI guide below for match guide) to crack the code
* Submit the correct code to escape before guesses or time runs out!

### UI Guide
<img src="ui_guide.png" width=600>
