package com.leonardo.apprh.controllers;

import com.leonardo.apprh.models.Candidato;
import com.leonardo.apprh.models.Vaga;
import com.leonardo.apprh.repository.CandidatoRepository;
import com.leonardo.apprh.repository.VagaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class VagaController {

    @Autowired
    private VagaRepository vr;

    @Autowired
    private CandidatoRepository cr;

    // CADASTRA VAGA
    @RequestMapping(value = "/cadastrarVaga", method = RequestMethod.GET)
    public String form() {
        return "vaga/formVaga";
    }

    @RequestMapping(value = "/cadastrarVaga", method = RequestMethod.POST)
    public String form(@Valid Vaga vaga, BindingResult result, RedirectAttributes attributes) {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("mensagem", "Verifique os campos...");
            return "redirect:/cadastrarVaga";
        }

        vr.save(vaga);
        attributes.addFlashAttribute("mensagem", "Vaga cadastrada com sucesso!");
        return "redirect:/cadastrarVaga";
    }

    // LISTA VAGAS
    @RequestMapping("/vagas")
    public ModelAndView listaVagas() {
        ModelAndView mv = new ModelAndView("vaga/listaVaga");
        List<Vaga> vagas = vr.findAll();
        mv.addObject("vagas", vagas);
        return mv;
    }

    // LISTA DETALHES VAGA
    @RequestMapping(value = "/{codigo}", method = RequestMethod.GET)
    public ModelAndView detalhesVaga(@PathVariable("codigo") String codigoStr) {
        try {
            long codigo = Long.parseLong(codigoStr);
            Vaga vaga = vr.findByCodigo(codigo);

            if (vaga != null) {
                ModelAndView mv = new ModelAndView("vaga/detalhesVaga");
                mv.addObject("vaga", vaga);

                List<Candidato> candidatos = cr.findByVaga(vaga);
                mv.addObject("candidatos", candidatos);

                return mv;
            } else {
                return new ModelAndView("redirect:/vagas");
            }
        } catch (NumberFormatException e) {
            return new ModelAndView("redirect:/vagas");
        }
    }

    // DELETA VAGA
    @RequestMapping("/deletarVaga")
    public String deletarVaga(@RequestParam String codigo) {
        try {
            long codigoLong = Long.parseLong(codigo);
            Vaga vaga = vr.findByCodigo(codigoLong);
            vr.delete(vaga);
            return "redirect:/vagas";
        } catch (NumberFormatException e) {
            return "redirect:/vagas";
        }
    }

    // ADICIONAR CANDIDATO
    @RequestMapping(value = "/{codigo}", method = RequestMethod.POST)
    public String detalhesVagaPost(@PathVariable("codigo") long codigo, @Valid Candidato candidato,
                                   BindingResult result, RedirectAttributes attributes) {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("mensagem", "Verifique os campos");
            return "redirect:/{codigo}";
        }

        // rg duplicado
        if (cr.findByRg(candidato.getRg()) != null) {
            attributes.addFlashAttribute("mensagem_erro", "RG duplicado");
            return "redirect:/{codigo}";
        }

        Vaga vaga = vr.findByCodigo(codigo);
        candidato.setVaga(vaga);
        cr.save(candidato);
        attributes.addFlashAttribute("mensagem", "Candidato adicionado com sucesso!");
        return "redirect:/{codigo}";
    }

    // DELETA CANDIDATO pelo RG
    @RequestMapping("/deletarCandidato")
    public String deletarCandidato(String rg) {
        Candidato candidato = cr.findByRg(rg);
        Vaga vaga = candidato.getVaga();
        String codigo = "" + vaga.getCodigo();

        cr.delete(candidato);

        return "redirect:/" + codigo;

    }

    // Métodos que atualizam vaga
    // formulário edição de vaga
    @RequestMapping(value = "/editar-vaga/{codigo}", method = RequestMethod.GET)
    public ModelAndView editarVaga(@PathVariable("codigo") long codigo) {
        Vaga vaga = vr.findByCodigo(codigo);
        ModelAndView mv = new ModelAndView("vaga/update-vaga");
        mv.addObject("vaga", vaga);
        return mv;
    }

    @RequestMapping(value = "/editar-vaga", method = RequestMethod.POST)
    public String updateVaga(@RequestParam("codigo") long codigo, @Valid Vaga vaga, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "vaga/update-vaga";
        }

        try {
            System.out.println("Código da vaga a ser editada: " + codigo);

            Vaga vagaExistente = vr.findByCodigo(codigo);

            if (vagaExistente != null) {
                vagaExistente.setNome(vaga.getNome());
                vagaExistente.setData(vaga.getData());
                vagaExistente.setSalario(vaga.getSalario());
                vagaExistente.setDescricao(vaga.getDescricao());

                vr.save(vagaExistente);
                attributes.addFlashAttribute("success", "Vaga alterada com sucesso!");
            } else {
                System.out.println("Vaga não encontrada para o código: " + codigo);
            }
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Erro ao atualizar a vaga.");
        }

        return "redirect:/vagas";
    }



}
