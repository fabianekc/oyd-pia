package eu.ownyourdata.pia.web.rest;

import com.codahale.metrics.annotation.Timed;
import eu.ownyourdata.pia.domain.Data;
import eu.ownyourdata.pia.repository.DataRepository;
import eu.ownyourdata.pia.web.rest.util.HeaderUtil;
import eu.ownyourdata.pia.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Data.
 */
@RestController
@RequestMapping("/api")
public class DataResource {

    private final Logger log = LoggerFactory.getLogger(DataResource.class);
        
    @Inject
    private DataRepository dataRepository;
    
    /**
     * POST  /datas -> Create a new data.
     */
    @RequestMapping(value = "/datas",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Data> createData(@Valid @RequestBody Data data) throws URISyntaxException {
        log.debug("REST request to save Data : {}", data);
        if (data.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("data", "idexists", "A new data cannot already have an ID")).body(null);
        }
        Data result = dataRepository.save(data);
        return ResponseEntity.created(new URI("/api/datas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("data", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /datas -> Updates an existing data.
     */
    @RequestMapping(value = "/datas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Data> updateData(@Valid @RequestBody Data data) throws URISyntaxException {
        log.debug("REST request to update Data : {}", data);
        if (data.getId() == null) {
            return createData(data);
        }
        Data result = dataRepository.save(data);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("data", data.getId().toString()))
            .body(result);
    }

    /**
     * GET  /datas -> get all the datas.
     */
    @RequestMapping(value = "/datas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Data>> getAllDatas(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Datas");
        Page<Data> page = dataRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/datas");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /datas/:id -> get the "id" data.
     */
    @RequestMapping(value = "/datas/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Data> getData(@PathVariable Long id) {
        log.debug("REST request to get Data : {}", id);
        Data data = dataRepository.findOne(id);
        return Optional.ofNullable(data)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /datas/:id -> delete the "id" data.
     */
    @RequestMapping(value = "/datas/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteData(@PathVariable Long id) {
        log.debug("REST request to delete Data : {}", id);
        dataRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("data", id.toString())).build();
    }
}