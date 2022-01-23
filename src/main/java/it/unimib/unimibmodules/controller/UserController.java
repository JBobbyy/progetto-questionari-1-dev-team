package it.unimib.unimibmodules.controller;

import it.unimib.unimibmodules.dto.SurveyDTO;
import it.unimib.unimibmodules.dto.UserDTO;
import it.unimib.unimibmodules.exception.NotFoundException;
import it.unimib.unimibmodules.factory.UserFactory;
import it.unimib.unimibmodules.model.Survey;
import it.unimib.unimibmodules.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller handling HTTP requests from User
 * @author Gianlorenzo Martini
 * @version 0.1.0
 */

@RestController
@RequestMapping("/api")
public class UserController extends DTOMapping<User, UserDTO> {
    
    /**
     * Instance of UserRepository that will be used to access the db.
     */
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository, ModelMapper modelMapper) {
    	super(modelMapper);
        this.userRepository = userRepository;

        modelMapper.createTypeMap(User.class, UserDTO.class)
                .addMappings(mapper -> {
                    mapper.map(User::getId, UserDTO::setId);
                    mapper.map(User::getName, UserDTO::setName);
                    mapper.map(User::getUsername, UserDTO::setUsername);
                });

        modelMapper.createTypeMap(UserDTO.class, User.class)
                .addMappings(mapper -> {
                    mapper.map(UserDTO::getId, User::setId);
                    mapper.map(UserDTO::getName, User::setName);
                    mapper.map(UserDTO::getUsername, User::setUsername);
                });
    }

    /**
     * Gets the User associated with the given id.
     * @param       id      the id of the user
     * @return              an HTTP response with status 200 and the UserDTO if the user has been found, 500 otherwise
     */
    @GetMapping(path = "/getUser/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) throws NotFoundException {

        User user = userRepository.get(id);

        return new ResponseEntity<>(convertToDTO(user), HttpStatus.OK);
    }

    /**
     * Logs the User into the website if the combination of username and password match.
     * @param       username        the username insert by the user
     * @param       password        the password insert by the user
     * @return                      an HTTP response with status 200 and the UserDTO if the user has been auth, 500 otherwise
     */
    @PostMapping(path = "/logInUser")
    public ResponseEntity<String> logInUser(@RequestParam String username, @RequestParam String password) throws NotFoundException {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User user = userRepository.getByUsername(username);

        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return new ResponseEntity<>("Login Successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Login failed.", HttpStatus.UNAUTHORIZED);
        }
    }
    
    
    @GetMapping("/getSurveysCreated")
    public ResponseEntity<List<SurveyDTO>> getSurveysCreated(@RequestParam (name = "username") String username) throws NotFoundException {

        User user = userRepository.getByUsername(username);
        Set<Survey> surveys  = user.getSurveysCreated();

        List<SurveyDTO> surveysDTO = new ArrayList<>();

        for (Survey survey : surveys) {
            SurveyDTO surveyDTO = new SurveyDTO();
            surveyDTO.setId(survey.getId());
            surveyDTO.setSurveyName(survey.getName());
            surveyDTO.setUserDTO(convertToDTO(survey.getUser()));
            surveysDTO.add(surveyDTO);
        }

        //logger.debug("Retrieved surveys created by user: " + username + ".");
        return new ResponseEntity<>(surveysDTO, HttpStatus.OK);
    }

    /**
     * Create a new User.
     * @param requestParams     Parameters insert in order to create a new User
     * @return                  an HTTP response with status 200 and the UserDTO if the user has been created, 500 otherwise
     */
    @PostMapping(path = "/signUpUser")
    public ResponseEntity<String> signUpUser(@RequestParam Map<String,String> requestParams) {

        try {
            User user = userRepository.getByUsername(requestParams.get("username"));
            return new ResponseEntity<>("Username already existing.", HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            User user = UserFactory.createUser(
                    requestParams.get("email"),
                    bCryptPasswordEncoder.encode(requestParams.get("password")),
                    requestParams.get("username"),
                    requestParams.get("name"),
                    requestParams.get("surname"));

            userRepository.add(user);

            return new ResponseEntity<>("User created.", HttpStatus.CREATED);
        }
    }

    /**
	 * Converts an instance of User to an instance of UserDTO
	 * @param       user	    an instance of user
	 * @return			        an instance of UserDTO, containing the serialized data of user
	 * @see DTOMapping#convertToDTO
	 */
	@Override
	public UserDTO convertToDTO(User user) {

		return modelMapper.map(user, UserDTO.class);
	}

	/**
	 * Converts an instance of UserDTO to an instance of User
	 * @param       userDTO	    an instance of UserDTO
	 * @return				    an instance of User, containing the deserialized data of UserDTO
	 * @see DTOMapping#convertToEntity
	 */
	@Override
	public User convertToEntity(UserDTO userDTO) {

		return modelMapper.map(userDTO, User.class);
	}
}