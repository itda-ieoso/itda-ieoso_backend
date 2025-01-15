package itda.ieoso.Assignment;

import org.springframework.stereotype.Service;

@Service
public class AssignmentService {

//    private final AssignmentRepository assignmentRepository;
//    private final CourseRepository courseRepository;
//
//    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
//        this.assignmentRepository = assignmentRepository;
//        this.courseRepository = courseRepository;
//    }
//
//    public void createAssignment(String courseId, String lectureId, String userId, String title, String description, Date dueDate) {
//        // 강사 권한 확인
//        if (!courseRepository.existsByCourseIdAndCreatedBy_UserId(courseId, userId)) {
//            throw new IllegalArgumentException("Only the course creator can create assignments.");
//        }
//
//        Assignment assignment = new Assignment();
//        assignment.setAssignmentId(UUID.randomUUID().toString());
//        assignment.setTitle(title);
//        assignment.setDescription(description);
//        assignment.setDueDate(dueDate);
//        assignment.setHwCreatedAt(new Date());
//        assignment.setHwUpdatedAt(new Date());
//        assignment.setCourse(courseRepository.findById(courseId).orElseThrow());
//
//        assignmentRepository.save(assignment);
//    }
//    // Update Assignment
//    public void updateAssignment(String courseId, String lectureId, String assignmentId,
//                                 String title, String description, Date dueDate) {
//        // Find the assignment
//        Assignment assignment = assignmentRepository.findById(assignmentId)
//                .orElseThrow(() -> new IllegalArgumentException("Assignment not found."));
//
//        // Update fields if provided
//        if (title != null) {
//            assignment.setTitle(title);
//        }
//        if (description != null) {
//            assignment.setDescription(description);
//        }
//        if (dueDate != null) {
//            assignment.setDueDate(dueDate);
//        }
//
//        // Save updated assignment
//        assignmentRepository.save(assignment);
//    }
//
//    // Delete Assignment
//    public void deleteAssignment(String courseId, String lectureId, String assignmentId) {
//        // Check if the assignment exists
//        Assignment assignment = assignmentRepository.findById(assignmentId)
//                .orElseThrow(() -> new IllegalArgumentException("Assignment not found."));
//
//        // Delete the assignment
//        assignmentRepository.delete(assignment);
//    }
//
//    // Get Assignment Details
//    public Assignment getAssignment(String courseId, String lectureId, String assignmentId) {
//        // Find the assignment by its ID
//        return assignmentRepository.findById(assignmentId)
//                .orElseThrow(() -> new IllegalArgumentException("Assignment not found."));
//    }
}