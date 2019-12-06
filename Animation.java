public class Animation extends Action {

    private Entity entity;
    private int repeatCount;

    public Animation(Entity entity, int repeatCount)
    {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler)
    {
        if(entity instanceof AnimatedEntity)
            ((AnimatedEntity)entity).nextImage();

        if (repeatCount != 1)
        {
            if(entity instanceof AnimatedEntity)
                scheduler.scheduleEvent(entity, (new Animation((AnimatedEntity)entity, Math.max(repeatCount - 1, 0))), ((AnimatedEntity)entity).getAnimationPeriod());

//            if(entity instanceof AnimatedEntity)
//                scheduler.scheduleEvent(entity, ((AnimatedEntity)entity).createAnimationAction(Math.max(repeatCount - 1, 0)), ((AnimatedEntity)entity).getAnimationPeriod());
        }
    }
}
