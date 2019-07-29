package tesseract.core;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import tesseract.EnableTesseractJob;
import tesseract.core.annotation.ClientJobDetail;
import tesseract.core.annotation.TesseractJob;

import java.util.Map;
import java.util.Set;


public class TesseractJobDetailRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private static int count = 0;
    private static final String NAME_FORMATTER = "clientJobDetail-%d";

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        String basePackage = getBasePackage(metadata);
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(
                TesseractJob.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        Set<BeanDefinition> candidateComponents = scanner
                .findCandidateComponents(basePackage);

        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                Assert.isTrue(!annotationMetadata.isInterface(),
                        "@TesseractJob 不能放在接口上");
                Map<String, Object> attributes = annotationMetadata
                        .getAnnotationAttributes(
                                TesseractJob.class.getCanonicalName());
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                        .genericBeanDefinition(ClientJobDetail.class);
                String triggerName = attributes.get("triggerName").toString();
                beanDefinitionBuilder.addPropertyValue("className", annotationMetadata.getClassName());
                beanDefinitionBuilder.addPropertyValue("triggerName", triggerName);
                BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinitionBuilder.getBeanDefinition(), String.format(NAME_FORMATTER, count++), new String[]{});
                BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
            }
        }
    }

    private String getBasePackage(AnnotationMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(EnableTesseractJob.class.getCanonicalName());
        return annotationAttributes.get("basePackage").toString();
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }
}
