package uz.neft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.neft.dto.MiningSystemDto;
import uz.neft.dto.UserDto;
import uz.neft.entity.MiningSystem;
import uz.neft.entity.Role;
import uz.neft.entity.User;
import uz.neft.payload.ApiResponse;
import uz.neft.repository.CollectionPointRepository;
import uz.neft.repository.MiningSystemRepository;
import uz.neft.utils.Converter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MiningSystemService {

    private final MiningSystemRepository miningSystemRepository;
    private final Converter converter;

    @Autowired
    public MiningSystemService(MiningSystemRepository miningSystemRepository, Converter converter) {
        this.miningSystemRepository = miningSystemRepository;
        this.converter = converter;
    }

    public ResponseEntity<?> save(MiningSystemDto dto) {
        try {
            if (dto.getId() != null) return converter.apiError400("id shouldn't be sent");
            MiningSystem miningSystem = new MiningSystem();
            miningSystem.setName(dto.getName());
            MiningSystem save = miningSystemRepository.save(miningSystem);
            MiningSystemDto miningSystemDto = converter.miningSysToMiningSysDto(save);
            return converter.apiSuccess201("Mining system added", miningSystemDto);
        } catch (Exception e) {
            e.printStackTrace();
            return converter.apiError409("Error Creating MiningSystem");
        }
    }

    public ResponseEntity<?> edit(MiningSystemDto dto) {
        try {
            if (dto.getId() == null) return converter.apiError400("id is null");

            MiningSystem editMiningSys;
            Optional<MiningSystem> byId = miningSystemRepository.findById(dto.getId());
            if (byId.isPresent()) {
                editMiningSys = byId.get();
                editMiningSys.setName(dto.getName());
                editMiningSys = miningSystemRepository.save(editMiningSys);
                MiningSystemDto miningSystemDto = converter.miningSysToMiningSysDto(editMiningSys);
                return converter.apiSuccess200("Mining system edited", miningSystemDto);
            }
            return converter.apiError404("Mining system not found");
        } catch (Exception e) {
            e.printStackTrace();
            return converter.apiError409("Error editing mining system");
        }
    }

    public ResponseEntity<?> delete(Integer id) {
        try {
            if (id != null) {
                Optional<MiningSystem> byId = miningSystemRepository.findById(id);
                if (byId.isPresent()) {
                    miningSystemRepository.deleteById(id);
                    return converter.apiSuccess200("Mining system deleted ");
                } else {
                    return converter.apiError404("Mining system not found");
                }
            }
            return converter.apiError400("Id null");
        } catch (Exception e) {
            e.printStackTrace();
            return converter.apiError409("Error in deleting mining system", e);
        }
    }

    public ResponseEntity<?> findAll() {
        try {
            List<MiningSystem> all = miningSystemRepository.findAll();
            List<MiningSystemDto> collect = all.stream().map(converter::miningSysToMiningSysDto).collect(Collectors.toList());
            return converter.apiSuccess200(collect);
        } catch (Exception e) {
            e.printStackTrace();
            return converter.apiError409("Error in fetching mining systems", e);
        }
    }

    public ResponseEntity<?> findById(Integer id) {
        try {
            if (id != null) {
                Optional<MiningSystem> byId = miningSystemRepository.findById(id);
                if (byId.isPresent()) {
                    MiningSystemDto miningSystemDto = converter.miningSysToMiningSysDto(byId.get());
                    return converter.apiSuccess200(miningSystemDto);
                } else {
                    return converter.apiError404("Mining system not found");
                }
            }
            return converter.apiError400("Mining system id is null");
        } catch (Exception e) {
            e.printStackTrace();
            return converter.apiError409("Error in finding mining system", e);
        }
    }


}
