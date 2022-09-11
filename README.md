# MyLifeEngine
<img width="1470" alt="Schermata 2022-09-11 alle 17 40 16" src="https://user-images.githubusercontent.com/34452508/189536522-7fa5c171-c6dc-439f-8183-598a44fe96ef.png">


Custom life engine written in Java using Java Swing framework

## The Cell
Every cell contains several values such as the hunger, epochs (its age), partner, its DNA and the world it lives.
It is formed by 4 arms and a head. Every arm could be a damage arm, eat arm or a reproduce arm.

Depending on the configuration of its arms the cell act differently to the other cells and the foods in the world:
- More damage arms means the cell is more evil and can eat other cell easily
- More eat arm means that the cell craves more for food
- More reproduce arms means the cell can produce more son

### DNA
The DNA contains all the useful information about the cell and its behaviour.
It contains the configuration of the 4 arms, the velocity of the cell, the fertility (how many son could produce), the evilness and the bias.
It can be generated from a mother and father's DNA, and can mutate itself with a probability of 10%
The DNA could be represented by a `hashcode`.

The Hashcode it's formed of 9 bits: 
- The first 3 bits represents the number of damage arms
- The other 3 bits represents the number of reproduce arms
- The last 3 bits represents the number of eat arms
Example: 001000011 -> 001 (1) eat arms and 011 (3) damage arms

### BIAS
Contains 3 ranges:
- The food range (how far the cell could see food)
- The awarness range (how far the cell could see other cells)
- The reproduce range(how far the cell could see possible partners)

All of theese 3 values are used to create the color of the head of the cell, so the values can range from 0 to 255.

## How the cell moves?
Every time a cell move (every frame) its hunger decrement by a small value and its epoch increment by 1.
- Then if it has a partner alive and with the possibility to reproduce it moves forward to it.
- Else if its target is alive it moves forward to it.
- Else if it hasn't a target it look for it, it could be a food or another cell (depends on its evilness and its hunger).
- If it's close enough to the target it eats it.
- At the end if its hunger is too low or its epochs is too high it dies (😥).

## When the cell can reproduce?
A cell can reproduce if:
- Its hunger is big enough (it's satiated) 
- Its epochs is big enough (it's not a child)
- It has an alive partner 
- Its fertility is greater than 0 (can make at least another son)

## The GUI
The program contains a simple rough GUI. 
It's possible to see the most common cell, the current year of the world, the current population, and the total amount of food in the world.
On the top of the screen it is possible to set specific values for the generation of a new cell.
In order to generate a cell it is possible by clicking on any point of the screen.

The cells with an `X` on the top means that the cell is targeted by another cell

<img width="315" alt="Schermata 2022-09-11 alle 17 42 25" src="https://user-images.githubusercontent.com/34452508/189536465-369d34ab-4e14-4b64-83ce-1d0b93d38e41.png">

The cells with `<3` on the top means that it has a partner and it's going to reproduce

<img width="219" alt="Schermata 2022-09-11 alle 17 39 10" src="https://user-images.githubusercontent.com/34452508/189536420-f0bcedcf-6be0-4e98-943b-081a1cdbd57f.png">
