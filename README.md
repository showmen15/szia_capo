Systemy zdecentralizowane i agentowe - CAPO
======

Wizja
------

Celem projektu jest stworzenie modułu pozwalającego na określenie lokalizacji robota mobilnego na podstawie odczytów z sensorów. Robot może poruszać się po określonych pomieszczeniach. W tym celu wykorzystany zostanie algorytm oparty na particle filter localization (Monte Carlo localization). Zamiast gęstej siatki czątek w środowisku, w każdym z pomieszczeń będzie znajdował się agent, określający prawdopobieństwo wystąpienia robota w wybranych punktach tego pokoju. Na podstawie danych zebranych przez agentów oraz cyklicznie otrzymywanych odczytów sensora wyznaczany będzie czas obliczneń dla poszczególych agentów w kolejnej iteracji.
