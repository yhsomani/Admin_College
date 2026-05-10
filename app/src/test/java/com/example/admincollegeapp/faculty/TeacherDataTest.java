package com.example.admincollegeapp.faculty;

import org.junit.Test;
import static org.junit.Assert.*;

public class TeacherDataTest {

    @Test
    public void testConstructorWithNonNullCategory() {
        String name = "John Doe";
        String email = "john@example.com";
        String post = "Professor";
        String image = "http://image.com/1.jpg";
        String category = "Computer Science";
        String key = "abc123";

        TeacherData teacher = new TeacherData(name, email, post, image, category, key);

        assertEquals(name, teacher.getName());
        assertEquals(email, teacher.getEmail());
        assertEquals(post, teacher.getPost());
        assertEquals(image, teacher.getImage());
        assertEquals(category, teacher.getCategory());
        assertEquals(key, teacher.getKey());
    }

    @Test
    public void testConstructorWithNullCategory() {
        String name = "Jane Doe";
        String email = "jane@example.com";
        String post = "Assistant Professor";
        String image = "http://image.com/2.jpg";
        String category = null;
        String key = "def456";

        TeacherData teacher = new TeacherData(name, email, post, image, category, key);

        assertEquals(name, teacher.getName());
        assertEquals(email, teacher.getEmail());
        assertEquals(post, teacher.getPost());
        assertEquals(image, teacher.getImage());
        assertEquals("Unknown", teacher.getCategory()); // Edge case: null category -> "Unknown"
        assertEquals(key, teacher.getKey());
    }

    @Test
    public void testSettersAndGetters() {
        TeacherData teacher = new TeacherData();

        teacher.setName("Test Name");
        assertEquals("Test Name", teacher.getName());

        teacher.setEmail("test@email.com");
        assertEquals("test@email.com", teacher.getEmail());

        teacher.setPost("Test Post");
        assertEquals("Test Post", teacher.getPost());

        teacher.setImage("http://test.image");
        assertEquals("http://test.image", teacher.getImage());

        teacher.setCategory("Test Category");
        assertEquals("Test Category", teacher.getCategory());

        teacher.setKey("test_key");
        assertEquals("test_key", teacher.getKey());
    }

    @Test
    public void testDefaultConstructor() {
        TeacherData teacher = new TeacherData();
        assertNotNull(teacher);
        assertNull(teacher.getName());
        assertNull(teacher.getEmail());
        assertNull(teacher.getPost());
        assertNull(teacher.getImage());
        assertNull(teacher.getCategory());
        assertNull(teacher.getKey());
    }
}
