package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dao.PositionDao;
import com.example.employeemanagementsystem.dto.create.PositionCreateDto;
import com.example.employeemanagementsystem.dto.get.PositionDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.PositionMapper;
import com.example.employeemanagementsystem.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionDao positionDao;

    @Mock
    private PositionMapper positionMapper;

    @InjectMocks
    private PositionService positionService;

    private Position testPosition;
    private PositionCreateDto testPositionCreateDto;
    private PositionDto testPositionDto;

    @BeforeEach
    void setUp() {
        testPosition = new Position();
        testPosition.setId(1L);
        testPosition.setName("Test Position");
        testPosition.setDescription("Test Description");

        testPositionCreateDto = new PositionCreateDto();
        testPositionCreateDto.setName("Test Position");
        testPositionCreateDto.setDescription("Test Description");

        testPositionDto = new PositionDto();
        testPositionDto.setId(1L);
        testPositionDto.setName("Test Position");
        testPositionDto.setDescription("Test Description");
    }

    @Test
    void getPositionById_WhenPositionExists_ShouldReturnPositionDto() {
        when(positionDao.findById(1L)).thenReturn(Optional.of(testPosition));
        when(positionMapper.toDto(testPosition)).thenReturn(testPositionDto);

        PositionDto result = positionService.getPositionById(1L);

        assertNotNull(result);
        assertEquals(testPositionDto.getId(), result.getId());
        assertEquals(testPositionDto.getName(), result.getName());
        verify(positionDao, times(1)).findById(1L);
        verify(positionMapper, times(1)).toDto(testPosition);
    }

    @Test
    void getPositionById_WhenPositionNotExists_ShouldThrowResourceNotFoundException() {
        when(positionDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> positionService.getPositionById(1L));

        assertEquals("Position not found with id 1", exception.getMessage());
        verify(positionDao, times(1)).findById(1L);
        verify(positionMapper, never()).toDto(any());
    }

    @Test
    void getAllPositions_ShouldReturnListOfPositionDtos() {
        List<Position> positions = Collections.singletonList(testPosition);
        when(positionDao.findAll()).thenReturn(positions);
        when(positionMapper.toDto(testPosition)).thenReturn(testPositionDto);

        List<PositionDto> result = positionService.getAllPositions();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testPositionDto.getId(), result.get(0).getId());
        verify(positionDao, times(1)).findAll();
        verify(positionMapper, times(1)).toDto(testPosition);
    }

    @Test
    void getAllPositions_EmptyList_ShouldReturnEmptyList() {
        when(positionDao.findAll()).thenReturn(Collections.emptyList());

        List<PositionDto> result = positionService.getAllPositions();

        assertTrue(result.isEmpty());
        verify(positionDao, times(1)).findAll();
        verify(positionMapper, never()).toDto(any());
    }

    @Test
    void createPosition_ValidDto_ShouldReturnPositionDto() {
        when(positionMapper.toEntity(testPositionCreateDto)).thenReturn(testPosition);
        when(positionDao.save(testPosition)).thenReturn(testPosition);
        when(positionMapper.toDto(testPosition)).thenReturn(testPositionDto);

        PositionDto result = positionService.createPosition(testPositionCreateDto);

        assertNotNull(result);
        assertEquals(testPositionDto.getId(), result.getId());
        assertEquals(testPositionDto.getName(), result.getName());
        verify(positionMapper, times(1)).toEntity(testPositionCreateDto);
        verify(positionDao, times(1)).save(testPosition);
        verify(positionMapper, times(1)).toDto(testPosition);
    }

    @Test
    void updatePosition_ValidDto_ShouldReturnUpdatedPositionDto() {
        when(positionDao.findById(1L)).thenReturn(Optional.of(testPosition));
        doNothing().when(positionMapper).updatePositionFromDto(testPositionCreateDto, testPosition);
        when(positionDao.save(testPosition)).thenReturn(testPosition);
        when(positionMapper.toDto(testPosition)).thenReturn(testPositionDto);

        PositionDto result = positionService.updatePosition(1L, testPositionCreateDto);

        assertNotNull(result);
        assertEquals(testPositionDto.getId(), result.getId());
        assertEquals(testPositionDto.getName(), result.getName());
        verify(positionDao, times(1)).findById(1L);
        verify(positionMapper, times(1)).updatePositionFromDto(testPositionCreateDto, testPosition);
        verify(positionDao, times(1)).save(testPosition);
        verify(positionMapper, times(1)).toDto(testPosition);
    }

    @Test
    void updatePosition_NonExistent_ShouldThrowResourceNotFoundException() {
        when(positionDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> positionService.updatePosition(1L, testPositionCreateDto));

        assertEquals("Position not found with id 1", exception.getMessage());
        verify(positionDao, times(1)).findById(1L);
        verify(positionMapper, never()).updatePositionFromDto(any(), any());
        verify(positionDao, never()).save(any());
    }

    @Test
    void deletePosition_WhenPositionExists_ShouldDeletePosition() {
        when(positionDao.existsById(1L)).thenReturn(true);
        doNothing().when(positionDao).deleteById(1L);

        positionService.deletePosition(1L);

        verify(positionDao, times(1)).existsById(1L);
        verify(positionDao, times(1)).deleteById(1L);
    }

    @Test
    void deletePosition_WhenPositionNotExists_ShouldThrowResourceNotFoundException() {
        when(positionDao.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> positionService.deletePosition(1L));

        assertEquals("Position not found with id 1", exception.getMessage());
        verify(positionDao, times(1)).existsById(1L);
        verify(positionDao, never()).deleteById(anyLong());
    }
}