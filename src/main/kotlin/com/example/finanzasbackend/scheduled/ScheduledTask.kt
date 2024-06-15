package com.example.finanzasbackend.scheduled

import com.example.finanzasbackend.model.credito.Credito
import com.example.finanzasbackend.model.credito.Cuota
import com.example.finanzasbackend.repository.CreditoRepository
import com.example.finanzasbackend.repository.CuotaRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


@Component
class ScheduledTask(private val creditoRepository: CreditoRepository) {
    private val log: Logger = LoggerFactory.getLogger(ScheduledTask::class.java)
    private val dateFormat = SimpleDateFormat("HH:mm:ss")

    @Scheduled(fixedRate = 1000*60*60)
    fun calcularInteresMoratorio(){
        val creditos:List<Credito> = creditoRepository.findAll();
        for(credito in creditos){
            credito.calcularMora()
            creditoRepository.save(credito)
        }
    }
}