package com.hackerrank.weather.controller;

import com.hackerrank.weather.model.Weather;
import com.hackerrank.weather.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping(path = "weather")
public class WeatherApiRestController {

    @Autowired
    private WeatherRepository repository;

    @GetMapping("")
    ResponseEntity<List<Weather>> test(@RequestParam Optional<String> date, @RequestParam Optional<List<String>> city, @RequestParam Optional<String> sort) {
        List<Weather> wts = repository.findAll();

        //TODO: fix the case-insensitive query
        if (city.isPresent())
        {
            ArrayList<Weather> selectedCity = new ArrayList<>();
            List<String> cities = city.get();
            for(String cityLoop : cities)
            {
                selectedCity.addAll(repository.findBycity(cityLoop));
            }
            wts.retainAll(selectedCity);
        }
        if (date.isPresent()) wts.retainAll(repository.findBydate(Date.from(Instant.parse(date.get()+"T00:00:00.000Z"))));
        if (sort.isPresent()) {
            if (sort.get().equals("date"))
                wts.sort(Comparator.comparing(a -> a.getDate()));
            if (sort.get().equals("-date"))
                wts.sort(Comparator.comparing(a -> ((Weather)a).getDate()).reversed());
        }
        return ResponseEntity.status(HttpStatus.OK).body(wts);
    }

    @PostMapping("")
    ResponseEntity<Weather> insertWeather(@RequestBody Weather newWeather) {
        repository.save(newWeather);
        return ResponseEntity.status(HttpStatus.CREATED).body(newWeather);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Weather> getByID(@PathVariable("id") Integer ID){
        System.out.println(ID);
        Weather res = repository.findByid(ID);
        if(res == null)
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        else return ResponseEntity.status(HttpStatus.OK).body(res);

    }
}
